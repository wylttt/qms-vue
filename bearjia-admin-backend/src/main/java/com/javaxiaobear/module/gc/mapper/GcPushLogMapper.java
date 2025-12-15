package com.javaxiaobear.module.gc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaxiaobear.module.gc.domain.entity.GcPushLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推送日志Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface GcPushLogMapper extends BaseMapper<GcPushLog> {

    /**
     * 查询推送日志列表
     * 
     * @param pushLog 查询条件
     * @return 日志列表
     */
    List<GcPushLog> selectPushLogList(GcPushLog pushLog);

    /**
     * 查询推送日志详情
     * 
     * @param logId 日志ID
     * @return 日志详情
     */
    GcPushLog selectPushLogById(@Param("logId") Long logId);

    /**
     * 新增推送日志
     * 
     * @param pushLog 日志信息
     * @return 影响行数
     */
    int insertPushLog(GcPushLog pushLog);

    /**
     * 修改推送日志
     * 
     * @param pushLog 日志信息
     * @return 影响行数
     */
    int updatePushLog(GcPushLog pushLog);

    /**
     * 删除推送日志
     * 
     * @param logId 日志ID
     * @return 影响行数
     */
    int deletePushLogById(@Param("logId") Long logId);

    /**
     * 批量删除推送日志
     * 
     * @param logIds 日志ID数组
     * @return 影响行数
     */
    int deletePushLogByIds(@Param("logIds") Long[] logIds);

    /**
     * 根据业务ID查询推送日志
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 日志列表
     */
    List<GcPushLog> selectPushLogByBusinessId(@Param("businessType") String businessType,
                                               @Param("businessId") Long businessId);

    /**
     * 查询需要重试的推送日志
     * 
     * @return 日志列表
     */
    List<GcPushLog> selectRetryPushLogs();
}
