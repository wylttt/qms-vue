package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcFollowUp;

import java.util.List;

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
     * @param followUp 随访对象
     * @return 随访对象集合
     */
    List<GcFollowUp> selectFollowUpList(GcFollowUp followUp);

    /**
     * 查询随访对象详情列表（包含居民和筛查结果信息）
     *
     * @param followUp 随访对象
     * @return 随访对象集合
     */
    List<GcFollowUp> selectFollowUpDetailList(GcFollowUp followUp);

    /**
     * 查询随访对象详情
     *
     * @param followUpId 随访对象主键
     * @return 随访对象
     */
    GcFollowUp selectFollowUpById(Long followUpId);

    /**
     * 根据居民ID查询随访对象
     *
     * @param residentId 居民ID
     * @return 随访对象列表
     */
    List<GcFollowUp> selectFollowUpByResidentId(Long residentId);

    /**
     * 新增随访对象
     *
     * @param followUp 随访对象
     * @return 结果
     */
    int insertFollowUp(GcFollowUp followUp);

    /**
     * 修改随访对象
     *
     * @param followUp 随访对象
     * @return 结果
     */
    int updateFollowUp(GcFollowUp followUp);

    /**
     * 批量删除随访对象
     *
     * @param followUpIds 需要删除的随访对象主键集合
     * @return 结果
     */
    int deleteFollowUpByIds(Long[] followUpIds);

    /**
     * 删除随访对象信息
     *
     * @param followUpId 随访对象主键
     * @return 结果
     */
    int deleteFollowUpById(Long followUpId);

    /**
     * 自动添加高风险人群为随访对象
     *
     * @param residentId 居民ID
     * @param resultId 筛查结果ID
     * @return 结果
     */
    int autoAddHighRiskFollowUp(Long residentId, Long resultId);

    /**
     * 分配随访任务
     *
     * @param followUpId 随访对象ID
     * @param assignedUserId 分配给的用户ID
     * @param assignedUserName 分配给的用户名称
     * @return 结果
     */
    int assignFollowUp(Long followUpId, Long assignedUserId, String assignedUserName);

    /**
     * 完成随访
     *
     * @param followUpId 随访对象ID
     * @return 结果
     */
    int completeFollowUp(Long followUpId);
}
