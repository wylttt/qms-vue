package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.GcPushLog;
import com.javaxiaobear.module.gc.mapper.GcPushLogMapper;
import com.javaxiaobear.module.gc.service.IGcPushLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推送日志Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcPushLogServiceImpl implements IGcPushLogService {
    
    @Autowired
    private GcPushLogMapper pushLogMapper;
    
    /**
     * 查询推送日志
     * 
     * @param logId 推送日志主键
     * @return 推送日志
     */
    @Override
    public GcPushLog selectPushLogById(Long logId) {
        return pushLogMapper.selectPushLogById(logId);
    }
    
    /**
     * 查询推送日志列表
     * 
     * @param pushLog 推送日志
     * @return 推送日志
     */
    @Override
    public List<GcPushLog> selectPushLogList(GcPushLog pushLog) {
        return pushLogMapper.selectPushLogList(pushLog);
    }
    
    /**
     * 新增推送日志
     * 
     * @param pushLog 推送日志
     * @return 结果
     */
    @Override
    public int insertPushLog(GcPushLog pushLog) {
        return pushLogMapper.insertPushLog(pushLog);
    }
    
    /**
     * 批量删除推送日志
     * 
     * @param logIds 需要删除的推送日志主键
     * @return 结果
     */
    @Override
    public int deletePushLogByIds(Long[] logIds) {
        return pushLogMapper.deletePushLogByIds(logIds);
    }
    
    /**
     * 删除推送日志信息
     * 
     * @param logId 推送日志主键
     * @return 结果
     */
    @Override
    public int deletePushLogById(Long logId) {
        return pushLogMapper.deletePushLogById(logId);
    }
    
    /**
     * 根据预约ID查询推送日志
     * 
     * @param appointmentId 预约ID
     * @return 推送日志列表
     */
    @Override
    public List<GcPushLog> selectPushLogByAppointmentId(Long appointmentId) {
        return pushLogMapper.selectPushLogByAppointmentId(appointmentId);
    }
    
    /**
     * 查询最近的推送日志
     * 
     * @param appointmentId 预约ID
     * @return 推送日志
     */
    @Override
    public GcPushLog selectLatestPushLog(Long appointmentId) {
        return pushLogMapper.selectLatestPushLog(appointmentId);
    }
}
