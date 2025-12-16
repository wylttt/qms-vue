package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcPushLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推送日志Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Mapper
public interface GcPushLogMapper {
    
    /**
     * 查询推送日志
     * 
     * @param logId 推送日志主键
     * @return 推送日志
     */
    public GcPushLog selectPushLogById(Long logId);
    
    /**
     * 查询推送日志列表
     * 
     * @param pushLog 推送日志
     * @return 推送日志集合
     */
    public List<GcPushLog> selectPushLogList(GcPushLog pushLog);
    
    /**
     * 新增推送日志
     * 
     * @param pushLog 推送日志
     * @return 结果
     */
    public int insertPushLog(GcPushLog pushLog);
    
    /**
     * 删除推送日志
     * 
     * @param logId 推送日志主键
     * @return 结果
     */
    public int deletePushLogById(Long logId);
    
    /**
     * 批量删除推送日志
     * 
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePushLogByIds(Long[] logIds);
    
    /**
     * 根据预约ID查询推送日志
     * 
     * @param appointmentId 预约ID
     * @return 推送日志列表
     */
    public List<GcPushLog> selectPushLogByAppointmentId(Long appointmentId);
    
    /**
     * 查询最近的推送日志
     * 
     * @param appointmentId 预约ID
     * @return 推送日志
     */
    public GcPushLog selectLatestPushLog(Long appointmentId);
}
