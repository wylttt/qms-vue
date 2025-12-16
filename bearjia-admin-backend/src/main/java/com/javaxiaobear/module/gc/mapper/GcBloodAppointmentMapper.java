package com.javaxiaobear.module.gc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.vo.BloodAppointmentVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 采血预约Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface GcBloodAppointmentMapper extends BaseMapper<GcBloodAppointment> {

    /**
     * 查询采血预约列表(关联居民、采血点、区域)
     * 
     * @param appointment 查询条件
     * @return 预约列表
     */
    List<BloodAppointmentVO> selectAppointmentList(GcBloodAppointment appointment);

    /**
     * 查询采血预约详情(关联居民、采血点、区域)
     * 
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    BloodAppointmentVO selectAppointmentDetail(@Param("appointmentId") Long appointmentId);

    /**
     * 新增采血预约
     * 
     * @param appointment 预约信息
     * @return 影响行数
     */
    int insertAppointment(GcBloodAppointment appointment);

    /**
     * 修改采血预约
     * 
     * @param appointment 预约信息
     * @return 影响行数
     */
    int updateAppointment(GcBloodAppointment appointment);

    /**
     * 删除采血预约
     * 
     * @param appointmentId 预约ID
     * @return 影响行数
     */
    int deleteAppointmentById(@Param("appointmentId") Long appointmentId);

    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 预约ID数组
     * @return 影响行数
     */
    int deleteAppointmentByIds(@Param("appointmentIds") Long[] appointmentIds);

    /**
     * 检查时间冲突(同一居民在同一天同一时间段已有预约)
     * 
     * @param residentId 居民ID
     * @param appointmentDate 预约日期
     * @param appointmentPeriod 预约时间段
     * @param appointmentId 当前预约ID(修改时排除自己)
     * @return 冲突数量
     */
    int checkTimeConflict(@Param("residentId") Long residentId,
                          @Param("appointmentDate") Date appointmentDate,
                          @Param("appointmentPeriod") String appointmentPeriod,
                          @Param("appointmentId") Long appointmentId);

    /**
     * 更新预约状态
     * 
     * @param appointmentId 预约ID
     * @param appointmentStatus 预约状态
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 影响行数
     */
    int updateAppointmentStatus(@Param("appointmentId") Long appointmentId,
                                 @Param("appointmentStatus") String appointmentStatus,
                                 @Param("operatorId") Long operatorId,
                                 @Param("operatorName") String operatorName);

    /**
     * 更新采样状态
     * 
     * @param appointmentId 预约ID
     * @param samplingStatus 采样状态
     * @param samplingTime 采样时间
     * @param samplerName 采样员姓名
     * @param sampleCode 采血样本编号
     * @return 影响行数
     */
    int updateSamplingStatus(@Param("appointmentId") Long appointmentId,
                             @Param("samplingStatus") String samplingStatus,
                             @Param("samplingTime") Date samplingTime,
                             @Param("samplerName") String samplerName,
                             @Param("sampleCode") String sampleCode);

    /**
     * 更新推送状态
     * 
     * @param appointmentId 预约ID
     * @param isPushed 是否已推送
     * @param pushTime 推送时间
     * @param pushStatus 推送状态
     * @param pushFailReason 推送失败原因
     * @param retryCount 重试次数
     * @return 影响行数
     */
    int updatePushStatus(@Param("appointmentId") Long appointmentId,
                         @Param("isPushed") Integer isPushed,
                         @Param("pushTime") Date pushTime,
                         @Param("pushStatus") String pushStatus,
                         @Param("pushFailReason") String pushFailReason,
                         @Param("retryCount") Integer retryCount);

    /**
     * 查询待推送的预约列表
     * 
     * @return 预约列表
     */
    List<GcBloodAppointment> selectPendingPushAppointments();

    /**
     * 查询推送失败且重试次数<3的预约列表
     * 
     * @return 预约列表
     */
    List<GcBloodAppointment> selectFailedPushAppointments();

    /**
     * 统计指定采血点的预约数量
     * 
     * @param siteId 采血点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约数量
     */
    int countAppointmentsBySite(@Param("siteId") Long siteId,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate);

    /**
     * 统计指定区域的采样完成数量
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 采样完成数量
     */
    int countSamplingByRegion(@Param("regionId") Long regionId,
                              @Param("regionLevel") Integer regionLevel,
                              @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate);

    /**
     * 统计指定采血点在指定日期的预约数量
     * 
     * @param siteId 采血点ID
     * @param appointmentDate 预约日期
     * @return 预约数量
     */
    int countAppointmentByDate(@Param("siteId") Long siteId,
                               @Param("appointmentDate") Date appointmentDate);
}
