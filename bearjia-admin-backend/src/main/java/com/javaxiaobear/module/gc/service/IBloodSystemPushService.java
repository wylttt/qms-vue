package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;

/**
 * 第三方系统推送Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IBloodSystemPushService {

    /**
     * 推送采血预约信息到第三方系统
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    boolean pushAppointmentToThirdSystem(Long appointmentId);

    /**
     * 批量推送采血预约信息
     * 
     * @param appointmentIds 预约ID数组
     * @return 成功推送数量
     */
    int batchPushAppointments(Long[] appointmentIds);

    /**
     * 重试推送失败的记录
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    boolean retryPushAppointment(Long appointmentId);

    /**
     * 处理第三方系统回调(采样状态更新)
     * 
     * @param appointmentId 预约ID
     * @param samplingStatus 采样状态
     * @param sampleCode 采血样本编号
     * @param samplerName 采样员姓名
     * @return 处理结果
     */
    boolean handleThirdSystemCallback(Long appointmentId, String samplingStatus, 
                                      String sampleCode, String samplerName);

    /**
     * 验证回调签名
     * 
     * @param signature 签名
     * @param timestamp 时间戳
     * @param data 数据
     * @return 验证结果
     */
    boolean validateCallbackSignature(String signature, String timestamp, String data);
}
