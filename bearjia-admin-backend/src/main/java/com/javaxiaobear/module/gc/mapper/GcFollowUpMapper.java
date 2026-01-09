package com.javaxiaobear.module.gc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访对象Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface GcFollowUpMapper extends BaseMapper<GcFollowUp> {

    /**
     * 查询随访对象列表(关联居民信息)
     * 
     * @param followUp 查询条件
     * @return 随访对象列表
     */
    List<Map<String, Object>> selectFollowUpList(GcFollowUp followUp);

    /**
     * 查询随访对象详情(关联居民、筛查结果)
     * 
     * @param followUpId 随访ID
     * @return 随访对象详情
     */
    Map<String, Object> selectFollowUpDetail(@Param("followUpId") Long followUpId);

    /**
     * 新增随访对象
     * 
     * @param followUp 随访对象
     * @return 影响行数
     */
    int insertFollowUp(GcFollowUp followUp);

    /**
     * 修改随访对象
     * 
     * @param followUp 随访对象
     * @return 影响行数
     */
    int updateFollowUp(GcFollowUp followUp);

    /**
     * 删除随访对象
     * 
     * @param followUpId 随访ID
     * @return 影响行数
     */
    int deleteFollowUpById(@Param("followUpId") Long followUpId);

    /**
     * 批量删除随访对象
     * 
     * @param followUpIds 随访ID数组
     * @return 影响行数
     */
    int deleteFollowUpByIds(@Param("followUpIds") Long[] followUpIds);

    /**
     * 更新随访状态
     * 
     * @param followUpId 随访ID
     * @param followUpStatus 随访状态
     * @param endDate 结束日期
     * @param followUpConclusion 随访结论
     * @return 影响行数
     */
    int updateFollowUpStatus(@Param("followUpId") Long followUpId,
                            @Param("followUpStatus") String followUpStatus,
                            @Param("endDate") Date endDate,
                            @Param("followUpConclusion") String followUpConclusion);

    /**
     * 更新实际随访次数和最后随访时间
     * 
     * @param followUpId 随访ID
     * @param actualVisitCount 实际随访次数
     * @param lastVisitTime 最后随访时间
     * @return 影响行数
     */
    int updateVisitCount(@Param("followUpId") Long followUpId,
                        @Param("actualVisitCount") Integer actualVisitCount,
                        @Param("lastVisitTime") Date lastVisitTime);

    /**
     * 根据筛查结果ID查询随访对象
     * 
     * @param resultId 筛查结果ID
     * @return 随访对象
     */
    GcFollowUp selectByResultId(@Param("resultId") Long resultId);

    /**
     * 根据居民ID查询随访对象列表
     * 
     * @param residentId 居民ID
     * @return 随访对象列表
     */
    List<Map<String, Object>> selectByResidentId(@Param("residentId") Long residentId);

    /**
     * 统计随访对象数量
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级(3:区/4:街道/5:社区)
     * @param followUpStatus 随访状态
     * @return 随访对象数量
     */
    int countFollowUp(@Param("regionId") Long regionId,
                     @Param("regionLevel") Integer regionLevel,
                     @Param("followUpStatus") String followUpStatus);

    /**
     * 查询待随访列表(用于提醒)
     * 
     * @return 待随访列表
     */
    List<Map<String, Object>> selectPendingFollowUps();

    /**
     * 查询随访中且超期未随访的列表
     * 
     * @param overdueDate 超期日期
     * @return 超期随访列表
     */
    List<Map<String, Object>> selectOverdueFollowUps(@Param("overdueDate") Date overdueDate);
}
