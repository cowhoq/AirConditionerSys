package org.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    @Getter
    private Map<String, Double> fanCost;

    @Getter
    private WorkMode workMode;

    @Getter
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
        log.info("主机工作参数: {}, {}, {}", workMode, range, fanCost.entrySet());
        return true;
    }


    /**
     * 关机
     *
     * @return 关机成功返回 true, 否则返回 false
     */
    public Boolean powerOff() {
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
        var url = "https://" + slaveIP + ":8080";
        var restTemplate = new RestTemplate();
        restTemplate.getForObject(url, R.class);
    }


    /**
     * 对请求队列中的请求进行调度
     * <p>
     * 目前的调度策略为时间片轮询, 轮询时长为 1 分钟
     */
    @Scheduled(fixedRate = 1000)
    private void schedule() {
        // 交给 spring boot 管理后, 主机没有启动(各项参数未初始化)就进行调度, 需要判断一下
        if (requestList == null)
            return;

        var size = requestList.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            // 从请求列表中获取第一次, 然后将其插入到队列末尾
            var request = requestList.removeFirst();
            requestList.addLast(request);
            if (checkRequestTemp(request)) {
                sendToSlave(slaveIPMap.get(request.getRoomId()));
                count++;
            }
            if (count == 3)
                break;
        }
    }
}
