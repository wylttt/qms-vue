package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcBloodAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采血预约Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Mapper
public interface GcBloodAppointmentMapper {
    
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
     * 删除采血预约
     * 
     * @param appointmentId 采血预约主键
     * @return 结果
     */
    public int deleteAppointmentById(Long appointmentId);
    
    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAppointmentByIds(Long[] appointmentIds);
    
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
     * 查询待推送的预约记录
     * 
     * @return 采血预约列表
     */
    public List<GcBloodAppointment> selectPendingPushAppointments();
    
    /**
     * 查询推送失败需要重试的预约记录
     * 
     * @param maxRetryTimes 最大重试次数
     * @return 采血预约列表
     */
    public List<GcBloodAppointment> selectFailedPushAppointments(@Param("maxRetryTimes") int maxRetryTimes);
    
    /**
     * 更新推送状态
     * 
     * @param appointmentId 预约ID
     * @param pushStatus 推送状态
     * @param thirdSystemId 第三方系统ID
     * @param barcodeNumber 条码编号
     * @return 结果
     */
    public int updatePushStatus(@Param("appointmentId") Long appointmentId,
                                @Param("pushStatus") String pushStatus,
                                @Param("thirdSystemId") String thirdSystemId,
                                @Param("barcodeNumber") String barcodeNumber);
    
    /**
     * 更新采样状态
     * 
     * @param appointmentId 预约ID
     * @param samplingStatus 采样状态
     * @return 结果
     */
    public int updateSamplingStatus(@Param("appointmentId") Long appointmentId,
                                     @Param("samplingStatus") String samplingStatus);
}
