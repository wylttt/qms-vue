package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访对象Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcFollowUpService {

    /**
     * 查询随访对象列表
     * 
     * @param followUp 查询条件
     * @return 随访对象列表
     */
    List<Map<String, Object>> selectFollowUpList(GcFollowUp followUp);

    /**
     * 查询随访对象详情
     * 
     * @param followUpId 随访ID
     * @return 随访对象详情
     */
    Map<String, Object> selectFollowUpDetail(Long followUpId);

    /**
     * 新增随访对象
     * 
     * @param followUp 随访对象
     * @return 新增结果
     */
    int insertFollowUp(GcFollowUp followUp);

    /**
     * 修改随访对象
     * 
     * @param followUp 随访对象
     * @return 修改结果
     */
    int updateFollowUp(GcFollowUp followUp);

    /**
     * 删除随访对象
     * 
     * @param followUpId 随访ID
     * @return 删除结果
     */
    int deleteFollowUpById(Long followUpId);

    /**
     * 批量删除随访对象
     * 
     * @param followUpIds 随访ID数组
     * @return 删除结果
     */
    int deleteFollowUpByIds(Long[] followUpIds);

    /**
     * 完成随访
     * 
     * @param followUpId 随访ID
     * @param followUpConclusion 随访结论
     * @return 更新结果
     */
    int completeFollowUp(Long followUpId, String followUpConclusion);

    /**
     * 根据居民ID查询随访对象列表
     * 
     * @param residentId 居民ID
     * @return 随访对象列表
     */
    List<Map<String, Object>> selectByResidentId(Long residentId);

    /**
     * 查询待随访列表
     * 
     * @return 待随访列表
     */
    List<Map<String, Object>> selectPendingFollowUps();

    /**
     * 查询超期随访列表
     * 
     * @param days 超期天数
     * @return 超期随访列表
     */
    List<Map<String, Object>> selectOverdueFollowUps(int days);
}
