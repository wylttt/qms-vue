package com.javaxiaobear.module.gc.task;

import com.javaxiaobear.module.gc.service.IGcBloodAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 采血预约推送定时任务
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Component
public class BloodAppointmentPushTask {
    
    private static final Logger log = LoggerFactory.getLogger(BloodAppointmentPushTask.class);
    
    @Autowired
    private IGcBloodAppointmentService appointmentService;
    
    /**
     * 定时批量推送待推送的预约
     * 每10分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void batchPushPendingAppointments() {
        log.info("开始执行批量推送待推送预约任务");
        
        try {
            int successCount = appointmentService.batchPushPendingAppointments();
            log.info("批量推送待推送预约任务完成，成功推送{}条记录", successCount);
        } catch (Exception e) {
            log.error("批量推送待推送预约任务执行失败", e);
        }
    }
    
    /**
     * 定时重试推送失败的预约
     * 每30分钟执行一次
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void retryFailedAppointments() {
        log.info("开始执行重试推送失败预约任务");
        
        try {
            int successCount = appointmentService.retryFailedAppointments();
            log.info("重试推送失败预约任务完成，成功推送{}条记录", successCount);
        } catch (Exception e) {
            log.error("重试推送失败预约任务执行失败", e);
        }
    }
}
