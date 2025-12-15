package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.module.gc.service.IBloodSystemPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方系统回调Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/blood/callback")
public class BloodSystemCallbackController extends BaseController {

    @Autowired
    private IBloodSystemPushService pushService;

    /**
     * 第三方系统回调接口(采样状态更新)
     * 
     * @param appointmentId 预约ID
     * @param samplingStatus 采样状态
     * @param sampleCode 采血样本编号
     * @param samplerName 采样员姓名
     * @param signature 签名
     * @param timestamp 时间戳
     * @return 处理结果
     */
    @PostMapping("/sampling-status")
    public AjaxResult updateSamplingStatus(@RequestParam Long appointmentId,
                                          @RequestParam String samplingStatus,
                                          @RequestParam String sampleCode,
                                          @RequestParam String samplerName,
                                          @RequestParam String signature,
                                          @RequestParam String timestamp) {
        // 1. 验证签名
        String data = String.format("appointmentId=%s&samplingStatus=%s&sampleCode=%s&samplerName=%s", 
            appointmentId, samplingStatus, sampleCode, samplerName);
        
        boolean valid = pushService.validateCallbackSignature(signature, timestamp, data);
        if (!valid) {
            return error("签名验证失败");
        }

        // 2. 处理回调
        boolean result = pushService.handleThirdSystemCallback(
            appointmentId, samplingStatus, sampleCode, samplerName
        );

        if (result) {
            return success("回调处理成功");
        } else {
            return error("回调处理失败");
        }
    }

    /**
     * 手动推送预约信息到第三方系统
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    @PostMapping("/push/{appointmentId}")
    public AjaxResult pushAppointment(@PathVariable Long appointmentId) {
        boolean result = pushService.pushAppointmentToThirdSystem(appointmentId);
        if (result) {
            return success("推送成功");
        } else {
            return error("推送失败");
        }
    }

    /**
     * 批量推送预约信息
     * 
     * @param appointmentIds 预约ID数组
     * @return 推送结果
     */
    @PostMapping("/push/batch")
    public AjaxResult batchPushAppointments(@RequestParam Long[] appointmentIds) {
        int successCount = pushService.batchPushAppointments(appointmentIds);
        return success("成功推送" + successCount + "条记录");
    }

    /**
     * 重试推送失败的记录
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    @PostMapping("/retry/{appointmentId}")
    public AjaxResult retryPush(@PathVariable Long appointmentId) {
        boolean result = pushService.retryPushAppointment(appointmentId);
        if (result) {
            return success("重试推送成功");
        } else {
            return error("重试推送失败");
        }
    }
}
