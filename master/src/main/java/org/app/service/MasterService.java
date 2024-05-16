package org.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.app.common.CheckWorkMode;
import org.app.common.R;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.graalvm.collections.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.System.exit;


/**
 * 主机服务类
 *
 * @author zfq
 */
@Slf4j
@Component
public class MasterService {
    @Autowired
    private RequestService requestService;

    @Value("classpath:config.json")
    Resource resource;

    private Pair<Integer, Integer> heatingDefaultTemp;

    private Pair<Integer, Integer> refrigerationDefaultTemp;

    private Map<String, Double> fanCost;

    @Getter
    private WorkMode workMode = WorkMode.OFF;   // 给 workMode 添加一个默认值

    private Pair<Integer, Integer> range;

    /**
     * 请求队列
     */
    private List<Request> requestList;

    /**
     * 从机请求 ip
     * <p>
     * 我们只在收到从机 powerOn 请求时记录其 IP, 并假定在收到 powerOff 请求之前其 IP 不发生改变.
     */
    private Map<Long, String> slaveIPMap;

    @Setter
    private Boolean TEST = false;


    /**
     * 获取默认参数
     */
    private void getDefaultParams() {
        try {
            var mapper = new ObjectMapper();
            Map<String, Object> config = mapper.readValue(resource.getInputStream(), Map.class);
            var list = (List) config.get("heating-default-temperature");
            heatingDefaultTemp = Pair.create((Integer) list.get(0), (Integer) list.get(1));
            list = (List) config.get("refrigeration-default-temperature");
            refrigerationDefaultTemp = Pair.create((Integer) list.get(0), (Integer) list.get(1));
            fanCost = (Map<String, Double>) config.get("fan-cost");
        } catch (IOException e) {
            log.error("读取配置文件失败, 请检查 resources 文件夹下是否存在 config.json");
            exit(1);
        } catch (Exception e) {
            log.error("配置文件格式错误, 请检查 config.json 格式");
            exit(1);
        }
    }

    /**
     * 开机, 并设置运行状态
     *
     * @return 开机成功返回 true, 否则返回 false
     */
    public Boolean powerOn(WorkMode workMode, Pair<Integer, Integer> range) {
        getDefaultParams();
        this.workMode = WorkMode.REFRIGERATION;
        this.range = refrigerationDefaultTemp;

        if (workMode != null) {
            this.workMode = workMode;
            if (this.workMode == WorkMode.HEATING)
                this.range = heatingDefaultTemp;
        }
        if (range != null)
            this.range = range;

        // 设置为线程安全性的集合
        this.requestList = Collections.synchronizedList(new LinkedList<>());
        this.slaveIPMap = Collections.synchronizedMap(new HashMap<>());
        log.info("主机启动成功!");
        log.info("主机工作参数: {}, {}, {}", this.workMode, this.range, fanCost.entrySet());
        return true;
    }


    /**
     * 关机
     *
     * @return 关机成功返回 true, 否则返回 false
     */
    public Boolean powerOff() {
        this.workMode = WorkMode.OFF;
        return true;
    }

    /**
     * 检查请求中的温度参数是否合法
     *
     * @param request 请求
     * @return 合法返回 true, 否则返回 false
     */
    private Boolean checkRequestTemp(Request request) {
        var startTemp = request.getStartTemp();
        var stopTemp = request.getStopTemp();

        // 检查设定温度是否在范围内
        if (stopTemp > range.getRight() || stopTemp < range.getLeft())
            return false;

        // 根据工作模式比较起始温度和停止温度
        return (workMode.equals(WorkMode.HEATING) && startTemp < stopTemp) ||
                (workMode.equals(WorkMode.REFRIGERATION) && startTemp > stopTemp);
    }


    /**
     * 接收到来自从机开机的请求
     *
     * @param request 从机的请求
     * @return 处理成功返回 true, 否则返回 false
     */
    @CheckWorkMode
    public Boolean slavePowerOn(Request request, String slaveIP) {
        // 判断请求队列是否有请求
        var _request = getRequest(request.getRoomId());
        if (_request != null)
            return false;

        if (!checkRequestTemp(request))
            return false;
        request.setStartTime(LocalDateTime.now());
        requestList.add(request);
        slaveIPMap.put(request.getRoomId(), slaveIP);
        return true;
    }

    /**
     * 根据 roomId 从 requestList 中获取请求
     *
     * @param roomId 从机的 roomId
     * @return 获取的请求
     */
    private Request getRequest(Long roomId) {
        var indexOpt = IntStream.range(0, requestList.size()).
                filter(i -> Objects.equals(requestList.get(i).getRoomId(), roomId)).
                findFirst();
        Request request = null;
        // 判断是否存在这么一个可关闭的请求
        if (indexOpt.isPresent())
            request = requestList.remove(indexOpt.getAsInt());
        return request;
    }


    /**
     * 从机改变工作参数
     *
     * @param roomId   从机的 roomId
     * @param fanSpeed 从机设定风速
     * @param temp     从机设定温度 (当 `fanSpeed == null` 时), 否则为从机当前温度
     * @return 处理成功返回 true, 否则返回 false
     */
    @CheckWorkMode
    public Boolean slaveChangeParams(Long roomId, String fanSpeed, Integer temp) {
        var request = getRequest(roomId);
        if (request == null)
            return false;
        if (fanSpeed != null) {
            // 改变风速时将原有请求结束, 并计算总花费存入数据库
            var stopTemp = request.getStopTemp();
            request.setStopTemp(temp); // 将当前室温作为结束室温
            calcFeeAndSave(request);

            // 更改请求参数, 并添加到请求队列中
            request.setStartTime(LocalDateTime.now());
            request.setStartTemp(temp); // 将当前室温写入初始室温
            request.setStopTemp(stopTemp);
            request.setFanSpeed(fanSpeed);
        } else
            request.setStopTemp(temp);

        requestList.add(request);
        return true;
    }


    /**
     * 计算一个请求的总花费并写入数据库
     *
     * @param request 请求
     */
    @CheckWorkMode
    private void calcFeeAndSave(Request request) {
        request.setStopTime(LocalDateTime.now());
        var seconds = Duration.between(request.getStartTime(), request.getStopTime()).toSeconds();
        var minutes = seconds / 60;
        if (seconds % 60 > 0)
            minutes++;
        request.setTotalFee(new BigDecimal(minutes * fanCost.get(request.getFanSpeed())));
        requestService.save(request);
    }


    /**
     * 接收到来自从机的关机请求
     * <p>
     * 将从机的请求从等待队列中移出, 并计算总费用
     *
     * @param roomId 从机的 roomId
     * @return 如果从机在请求列表中，则返回 true, 否则返回 false
     */
    @CheckWorkMode
    public Boolean slavePowerOff(Long roomId) {
        var request = getRequest(roomId);
        if (request == null)
            return false;
        calcFeeAndSave(request);
        slaveIPMap.remove(roomId);
        return true;
    }

    /**
     * 向从机发送送风请求 (GET)
     *
     * @param slaveIP 从机 ip
     */
    private void sendToSlave(String slaveIP) {
        var url = "https://" + slaveIP + ":8080" + "/send";
        var restTemplate = new RestTemplate();
        restTemplate.getForObject(url, R.class);
    }


    /**
     * 对请求队列中的请求进行调度
     * <p>
     * 目前的调度策略为时间片轮询, 轮询时长为 1 分钟
     */
    @Scheduled(fixedRate = 1000)
    public void schedule() {
        // 交给 spring boot 管理后, 主机没有启动(各项参数未初始化)就进行调度, 需要判断一下
        if (workMode.equals(WorkMode.OFF))
            return;

        var size = requestList.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            // 从请求列表中获取第一次, 然后将其插入到队列末尾
            var request = requestList.removeFirst();
            requestList.addLast(request);
            if (checkRequestTemp(request)) {
                if (TEST) // 如果是 TEST 模式, 则打印请求信息
                    log.info("{}", request);
                else
                    sendToSlave(slaveIPMap.get(request.getRoomId()));
                count++;
            }
            if (count == 3)
                break;
        }
    }


    /**
     * 供从机调用, 实时获取本次请求所需要的消耗的能量和所需支付的金额
     * <p>
     * 能量和金额都是使用了 `BigDecimal` 类, 可能不太方便.
     * TODO: 后续可能会因此调整为 `Double` 类型
     * <p>
     * TODO: 值得注意的是, 如果从机修改了风速请求, 则相当于重新发送一次请求, 费用从 0 开始计算,
     *       而从机改变温度则不会, 这一点可能会根据后续业务进行调整
     *
     * @param roomId 从机的 roomId
     * @return 消耗的能量和金额
     */
    public Pair<BigDecimal, BigDecimal> getEnergyAndFee(Long roomId) {
        var optionalRequest = requestList.stream().filter(i -> i.getRoomId().equals(roomId)).findFirst();

        if (optionalRequest.isPresent()) {
            var request = optionalRequest.get();
            var now = LocalDateTime.now();
            var seconds = Duration.between(request.getStartTime(), now).toSeconds();
            var minutes = seconds / 60;
            if (seconds % 60 > 0)
                minutes++;
            var energy = new BigDecimal(minutes * fanCost.get(request.getFanSpeed()));
            return Pair.create(energy, energy.multiply(new BigDecimal(5)));
        }
        return null;
    }

    /**
     * 获取主机工作的温度范围
     * <p>
     * 原先这部分是使用 `@Getter` 实现的, 后来加入了AOP, 所以改为了显示地写出了这部分代码
     */
    @CheckWorkMode
    public Pair<Integer, Integer> getRange() {
        return range;
    }
}
