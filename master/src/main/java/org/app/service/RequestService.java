package org.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.app.entity.dto.Period;
import org.app.entity.Request;
import org.app.mapper.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @author zfq
 */
@Service
public class RequestService extends ServiceImpl<RequestMapper, Request> {
    @Autowired
    RoomService roomService;

    /**
     * 查询房间的全部请求
     * <p>
     * 这个函数的作用是用户获取账单时使用, 将用户的数据一次性返还给请求方.
     * 请求方根据自己的需要计算相应的数据
     *
     * @return 请求列表
     */
    public List<Request> getRequestListByRoomId(Long roomId) {
        var lqw = new LambdaQueryWrapper<Request>();
        lqw.eq(Request::getRoomId, roomId);
        return this.list(lqw);
    }

    /**
     * 通过时间获取请求, 完成日常报表工作
     * <p>
     * TODO: 这个方法对应于需求文档中的第12条, 第12条所需要的信息更为具体,
     *       所以我希望放到接口层完成, 如果有需要可以分成不同层完成此工作
     */
    public List<Request> getRequestListByPeriod(Period period) {
        switch (period) {
            // 获取月度报表, 即获取上个月的报表
            case MONTH -> {
                var now = LocalDate.now();
                var prevYearMonth = YearMonth.from(now.getMonth().minus(1));
                LocalDate start = prevYearMonth.atDay(1), end = prevYearMonth.atEndOfMonth();
                var lqw = new LambdaQueryWrapper<Request>();
                lqw.between(Request::getStartTime, start, end);
                return this.list(lqw);
            }
            case WEEK -> {
                var now = LocalDate.now();
                LocalDate start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        end = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                var lqw = new LambdaQueryWrapper<Request>();
                lqw.between(Request::getStartTime, start, end);
                return this.list(lqw);
            }
            case DAY -> {
                var now = LocalDate.now();
                var lastDay = now.minusDays(1);
                var lqw = new LambdaQueryWrapper<Request>();
                lqw.between(Request::getStartTime, lastDay, lastDay);
                return this.list(lqw);
            }
        }
        return null;
    }
    public List<Request> getRoomRequestList(Long roomId){
        LambdaQueryWrapper<Request> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Request::getRoomId, roomId);
        return this.list(lqw);
    }
}
