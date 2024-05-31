package org.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.app.aop.CheckWorkMode;
import org.app.entity.Request;
import org.app.entity.WorkMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.System.exit;
import static org.app.MasterApplication.TEST;


/**
 * 主机服务类
 *
 * @author zfq
 */
@Slf4j
@Service
public class MasterService {
    @Autowired
    private RequestService requestService;

    @Autowired
    private RoomService roomService;

    @Value("classpath:config.json")
    Resource resource;

    private List<Integer> heatingDefaultTemp;

    private List<Integer> refrigerationDefaultTemp;

    private Map<String, Double> fanCost;

    @Getter
    @Setter
    private WorkMode workMode = WorkMode.OFF;   // 给 workMode 添加一个默认值

    private List<Integer> range;

    /**
     * 请求队列
     */
    private List<Request> requestList;

    /**
     * 保存在一个时间片内可以送风的房间 id
     */
    private Set<Long> sendAirRoomId;



    @PostConstruct
    public void init() {
        // 如果时测试模式时, 默认是制冷模式,
        // 免去在没有前端界面的情况下, 通过浏览器启动
        // 但是这样就无法获在启动时传递工作参数
        if (TEST)
            powerOn(null, null);
    }

    /**
     * 获取默认参数
     */
    private void getDefaultParams() {
        try {
            var mapper = new ObjectMapper();
            Map<String, Object> config = mapper.readValue(resource.getInputStream(), Map.class);
            heatingDefaultTemp = (List<Integer>) config.get("heating-default-temperature");
            refrigerationDefaultTemp = (List<Integer>) config.get("refrigeration-default-temperature");
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
    public Boolean powerOn(WorkMode workMode, List<Integer> range) {
        if (this.workMode != WorkMode.OFF)
            return false;
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
        this.sendAirRoomId = Collections.synchronizedSet(new HashSet<>());
        log.info("主机启动成功! 主机工作参数: {}, {}, {}", this.workMode, this.range, fanCost.entrySet());
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
        if (stopTemp > range.get(1) || stopTemp < range.get(0))
            return false;

        // 根据工作模式比较起始温度和停止温度
        return (workMode.equals(WorkMode.HEATING) && startTemp < stopTemp) ||
                (workMode.equals(WorkMode.REFRIGERATION) && startTemp > stopTemp);
    }


    /**
     * 接收到来自从机的请求
     * <p>
     * 无论收到的请求是开机, 还是调整工作参数 都应该调用这个函数,
     * 相当于`slavePowerOn` 与 `slaveChangeParams` 合并了
     *
     * @param newRequest 从机的请求
     * @return 处理成功返回返回上一次请求所消耗的费用
     * (如果没有上一次的请求, 则返回 [0, 0]), 否则返回 null
     */
    @CheckWorkMode
    public List<BigDecimal> slaveRequest(Request newRequest) {
        // 判断请求队列是否有请求
        var oldRequest = getRequest(newRequest.getRoomId());

        if (oldRequest == null) {
            if (!checkRequestTemp(newRequest))
                return null;
            newRequest.setStartTime(LocalDateTime.now());
            requestList.add(newRequest);
            return new ArrayList<>(Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO));
        } else {
            if (checkRequestTemp(newRequest)) { // 温度校验合格
                newRequest.setStartTime(LocalDateTime.now());
                requestList.add(newRequest);
                // 存入旧的请求时, 更改旧请求的停止温度, 验收时查出来的小错误
                oldRequest.setStopTemp(newRequest.getStartTemp());
                return calcFeeAndSave(oldRequest);
            } else {
                requestList.add(oldRequest);
                return null;
            }
        }
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
     * 计算一个请求的总花费并写入数据库
     *
     * @param request 请求
     * @return 返回一个包含 energy, fee 的 list
     */
    @CheckWorkMode
    private List<BigDecimal> calcFeeAndSave(Request request) {
        request.setStopTime(LocalDateTime.now());
        var seconds = Duration.between(request.getStartTime(), request.getStopTime()).toSeconds();
        var minutes = seconds / 60;
        if (seconds % 60 > 0)
            minutes++;
        var energy = new BigDecimal(minutes * fanCost.get(request.getFanSpeed()));
        var fee = energy.multiply(new BigDecimal(5));
        request.setTotalFee(fee);

        var roomId = request.getRoomId();
        var userId = roomService.getById(roomId).getUserId();
        request.setUserId(userId);
        requestService.save(request);
        return new ArrayList<>(List.of(energy, fee));
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
    public List<BigDecimal> slavePowerOff(Long roomId) {
        var request = getRequest(roomId);
        if (request == null)
            return null;
        return calcFeeAndSave(request);
    }

    /**
     * 获取所有从机状态
     */
    @CheckWorkMode
    public List<Request> getSlaveList() {
        return requestList;
    }

    /**
     * 对请求队列中的请求进行调度
     * <p>
     * 目前的调度策略为时间片轮询, 轮询时长为 1 分钟
     */
    @Scheduled(fixedRateString = "${master.fixedRate}")
    public void schedule() {
        // 交给 spring boot 管理后, 主机没有启动(各项参数未初始化)就进行调度, 需要判断一下
        if (workMode.equals(WorkMode.OFF))
            return;
        // 每次添加之前记得清零......
        sendAirRoomId.clear();
        var size = requestList.size();
        if (size == 0)
            return;
        else if (size <= 3)       // 如果请求数 <= 3, 主机完全可以调度地过来直接添加到 sendAirRoomId 中即可
            sendAirRoomId.addAll(requestList.stream().map(Request::getRoomId).toList());
        else {
            var count = 0;
            for (var i = 0; i < size; i++) {
                // 从请求列表中获取第一次, 然后将其插入到队列末尾
                var request = requestList.removeFirst();
                requestList.addLast(request);
                if (checkRequestTemp(request)) {
                    sendAirRoomId.add(request.getRoomId());
                    count++;
                }
                if (count == 3)
                    break;
            }
        }
        if (TEST)
            log.info("本次调度请求有: {}", sendAirRoomId);
    }

    /**
     * 供从机和前端调用, 实时获取本次请求所需要的消耗的能量和所需支付的金额
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
    public List<BigDecimal> getEnergyAndFee(Long roomId) {
        var optionalRequest = requestList.stream().filter(i -> i.getRoomId().equals(roomId)).findFirst();

        if (optionalRequest.isPresent()) {
            var request = optionalRequest.get();
            var now = LocalDateTime.now();
            var seconds = Duration.between(request.getStartTime(), now).toSeconds();
            var minutes = seconds / 60;
            if (seconds % 60 > 0)
                minutes++;
            var energy = new BigDecimal(minutes * fanCost.get(request.getFanSpeed()));
            return new ArrayList<>(List.of(energy, energy.multiply(new BigDecimal(5))));
        }
        return null;
    }

    /**
     * 获取主机工作的温度范围
     * <p>
     * 原先这部分是使用 `@Getter` 实现的, 后来加入了AOP, 所以改为了显示地写出了这部分代码
     */
    @CheckWorkMode
    public List<Integer> getRange() {
        return range;
    }

    @CheckWorkMode
    public void setRange(List<Integer> range) {
        this.range = range;
    }

    @CheckWorkMode
    public Boolean contains(Long roomId) {
        return sendAirRoomId.contains(roomId);
    }
}
