package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcBloodAppointment;

import java.util.List;

/**
 * 采血预约Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcBloodAppointmentService {
    
    /**
     * 查询采血预约
     * 
     * @param appointmentId 采血预约主键
     * @return 采血预约
     */
    public GcBloodAppointment selectAppointmentById(Long appointmentId);
    
    /**
     * 查询采血预约列表
     * 
     * @param appointment 采血预约
     * @return 采血预约集合
     */
    public List<GcBloodAppointment> selectAppointmentList(GcBloodAppointment appointment);
    
    /**
     * 新增采血预约
     * 
     * @param appointment 采血预约
     * @return 结果
     */
    public int insertAppointment(GcBloodAppointment appointment);
    
    /**
     * 修改采血预约
     * 
     * @param appointment 采血预约
     * @return 结果
     */
    public int updateAppointment(GcBloodAppointment appointment);
    
    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 需要删除的采血预约主键集合
     * @return 结果
     */
    public int deleteAppointmentByIds(Long[] appointmentIds);
    
    /**
     * 删除采血预约信息
     * 
     * @param appointmentId 采血预约主键
     * @return 结果
     */
    public int deleteAppointmentById(Long appointmentId);
    
    /**
     * 根据居民ID查询预约记录
     * 
     * @param residentId 居民ID
     * @return 采血预约列表
     */
    public List<GcBloodAppointment> selectAppointmentByResidentId(Long residentId);
    
    /**
     * 根据采血点ID查询预约记录
     * 
     * @param siteId 采血点ID
     * @return 采血预约列表
     */
    public List<GcBloodAppointment> selectAppointmentBySiteId(Long siteId);
    
    /**
     * 取消预约
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int cancelAppointment(Long appointmentId);
    
    /**
     * 推送预约信息到第三方系统
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public boolean pushToThirdSystem(Long appointmentId);
    
    /**
     * 批量推送待推送的预约
     * 
     * @return 推送成功数量
     */
    public int batchPushPendingAppointments();
    
    /**
     * 重试推送失败的预约
     * 
     * @return 推送成功数量
     */
    public int retryFailedAppointments();
    
    /**
     * 接收第三方系统回调更新采样状态
     * 
     * @param thirdSystemId 第三方系统预约ID
     * @param samplingStatus 采样状态
     * @param barcodeNumber 条码编号
     * @return 结果
     */
    public boolean receiveThirdSystemCallback(String thirdSystemId, String samplingStatus, String barcodeNumber);
}
