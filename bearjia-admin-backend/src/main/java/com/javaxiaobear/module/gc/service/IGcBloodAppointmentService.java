package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.vo.BloodAppointmentVO;

import java.util.Date;
import java.util.List;

/**
 * 采血预约Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcBloodAppointmentService {

    /**
     * 查询采血预约列表
     * 
     * @param appointment 查询条件
     * @return 预约列表
     */
    List<BloodAppointmentVO> selectAppointmentList(GcBloodAppointment appointment);

    /**
     * 查询采血预约详情
     * 
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    BloodAppointmentVO selectAppointmentDetail(Long appointmentId);

    /**
     * 新增采血预约
     * 
     * @param appointment 预约信息
     * @return 新增结果
     */
    int insertAppointment(GcBloodAppointment appointment);

    /**
     * 修改采血预约
     * 
     * @param appointment 预约信息
     * @return 修改结果
     */
    int updateAppointment(GcBloodAppointment appointment);

    /**
     * 删除采血预约
     * 
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    int deleteAppointmentById(Long appointmentId);

    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 预约ID数组
     * @return 删除结果
     */
    int deleteAppointmentByIds(Long[] appointmentIds);

    /**
     * 确认预约
     * 
     * @param appointmentId 预约ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 更新结果
     */
    int confirmAppointment(Long appointmentId, Long operatorId, String operatorName);

    /**
     * 取消预约
     * 
     * @param appointmentId 预约ID
     * @param cancelReason 取消原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 更新结果
     */
    int cancelAppointment(Long appointmentId, String cancelReason, Long operatorId, String operatorName);

    /**
     * 更新采样状态
     * 
     * @param appointmentId 预约ID
     * @param samplerName 采样员姓名
     * @param sampleCode 采血样本编号
     * @return 更新结果
     */
    int updateSamplingStatus(Long appointmentId, String samplerName, String sampleCode);

    /**
     * 批量预约
     * 
     * @param residentIds 居民ID列表
     * @param appointmentSiteId 采血点ID
     * @param appointmentDate 预约日期
     * @param appointmentPeriod 预约时间段
     * @return 成功预约数量
     */
    int batchAppointment(List<Long> residentIds, Long appointmentSiteId, Date appointmentDate, String appointmentPeriod);

    /**
     * 导出预约列表
     * 
     * @param appointment 查询条件
     * @return 预约列表
     */
    List<BloodAppointmentVO> exportAppointmentList(GcBloodAppointment appointment);
}
