package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.utils.DateUtils;
import com.javaxiaobear.module.gc.domain.GcFollowUp;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.service.IGcFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 随访对象Service业务层处理
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcFollowUpServiceImpl implements IGcFollowUpService {
    @Autowired
    private GcFollowUpMapper followUpMapper;

    /**
     * 查询随访对象列表
     *
     * @param followUp 随访对象
     * @return 随访对象
     */
    @Override
    public List<GcFollowUp> selectFollowUpList(GcFollowUp followUp) {
        return followUpMapper.selectFollowUpList(followUp);
    }

    /**
     * 查询随访对象详情列表（包含居民和筛查结果信息）
     *
     * @param followUp 随访对象
     * @return 随访对象集合
     */
    @Override
    public List<GcFollowUp> selectFollowUpDetailList(GcFollowUp followUp) {
        return followUpMapper.selectFollowUpDetailList(followUp);
    }

    /**
     * 查询随访对象详情
     *
     * @param followUpId 随访对象主键
     * @return 随访对象
     */
    @Override
    public GcFollowUp selectFollowUpById(Long followUpId) {
        return followUpMapper.selectFollowUpById(followUpId);
    }

    /**
     * 根据居民ID查询随访对象
     *
     * @param residentId 居民ID
     * @return 随访对象列表
     */
    @Override
    public List<GcFollowUp> selectFollowUpByResidentId(Long residentId) {
        return followUpMapper.selectFollowUpByResidentId(residentId);
    }

    /**
     * 新增随访对象
     *
     * @param followUp 随访对象
     * @return 结果
     */
    @Override
    public int insertFollowUp(GcFollowUp followUp) {
        return followUpMapper.insertFollowUp(followUp);
    }

    /**
     * 修改随访对象
     *
     * @param followUp 随访对象
     * @return 结果
     */
    @Override
    public int updateFollowUp(GcFollowUp followUp) {
        return followUpMapper.updateFollowUp(followUp);
    }

    /**
     * 批量删除随访对象
     *
     * @param followUpIds 需要删除的随访对象主键
     * @return 结果
     */
    @Override
    public int deleteFollowUpByIds(Long[] followUpIds) {
        return followUpMapper.deleteFollowUpByIds(followUpIds);
    }

    /**
     * 删除随访对象信息
     *
     * @param followUpId 随访对象主键
     * @return 结果
     */
    @Override
    public int deleteFollowUpById(Long followUpId) {
        return followUpMapper.deleteFollowUpById(followUpId);
    }

    /**
     * 自动添加高风险人群为随访对象
     *
     * @param residentId 居民ID
     * @param resultId 筛查结果ID
     * @return 结果
     */
    @Override
    @Transactional
    public int autoAddHighRiskFollowUp(Long residentId, Long resultId) {
        // 检查是否已存在随访记录
        Integer existCount = followUpMapper.checkFollowUpExists(residentId, resultId);
        if (existCount > 0) {
            return 0; // 已存在，不重复添加
        }

        // 创建随访记录
        GcFollowUp followUp = new GcFollowUp();
        followUp.setResidentId(residentId);
        followUp.setResultId(resultId);
        followUp.setFollowUpType("blood_high_risk"); // 血液筛查高风险
        followUp.setFollowUpStatus("pending"); // 待随访
        followUp.setFollowUpLevel("high"); // 高优先级
        followUp.setRemark("系统自动加入：血液筛查结果为高风险");

        return followUpMapper.insertFollowUp(followUp);
    }

    /**
     * 分配随访任务
     *
     * @param followUpId 随访对象ID
     * @param assignedUserId 分配给的用户ID
     * @param assignedUserName 分配给的用户名称
     * @return 结果
     */
    @Override
    @Transactional
    public int assignFollowUp(Long followUpId, Long assignedUserId, String assignedUserName) {
        GcFollowUp followUp = new GcFollowUp();
        followUp.setFollowUpId(followUpId);
        followUp.setAssignedUserId(assignedUserId);
        followUp.setAssignedUserName(assignedUserName);
        followUp.setAssignDate(DateUtils.getNowDate());
        followUp.setFollowUpStatus("assigned"); // 已分配

        return followUpMapper.updateFollowUp(followUp);
    }

    /**
     * 完成随访
     *
     * @param followUpId 随访对象ID
     * @return 结果
     */
    @Override
    @Transactional
    public int completeFollowUp(Long followUpId) {
        GcFollowUp followUp = new GcFollowUp();
        followUp.setFollowUpId(followUpId);
        followUp.setFollowUpStatus("completed"); // 已完成
        followUp.setCompleteDate(DateUtils.getNowDate());

        return followUpMapper.updateFollowUp(followUp);
    }
}
