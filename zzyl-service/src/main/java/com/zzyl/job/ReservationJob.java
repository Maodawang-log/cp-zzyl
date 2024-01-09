package com.zzyl.job;

import com.zzyl.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author itheima
 */
@Component
@Slf4j
public class ReservationJob {

    @Autowired
    private ReservationService reservationService;

    /**
     * 预约过期修改<br>
     * 每半个小时执行一次
     */
    @Scheduled(cron = "0 1,31 * * * ? ")
    public void updateReservationStatus() {
        log.info("预约状态-过期修改-begin");
        reservationService.updateReservationStatus(LocalDateTime.now());
        log.info("预约状态-过期修改-end");
    }
}
