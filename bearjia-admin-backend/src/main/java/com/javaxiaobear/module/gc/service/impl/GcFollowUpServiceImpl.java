package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcFollowUpTrackMapper;
import com.javaxiaobear.module.gc.service.IGcFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访对象Service实现类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcFollowUpServiceImpl implements IGcFollowUpService {

    @Autowired
    private GcFollowUpMapper followUpMapper;

    @Autowired
    private GcFollowUpTrackMapper followUpTrackMapper;

    /**
     * 查询随访对象列表
     * 
     * @param followUp 查询条件
     * @return 随访对象列表
     */
    @Override
    public List<Map<String, Object>> selectFollowUpList(GcFollowUp followUp) {
        return followUpMapper.selectFollowUpList(followUp);
    }

    /**
     * 查询随访对象详情
     * 
     * @param followUpId 随访ID
     * @return 随访对象详情
     */
    @Override
    public Map<String, Object> selectFollowUpDetail(Long followUpId) {
        if (followUpId == null) {
            throw new ServiceException("随访ID不能为空");
        }
        return followUpMapper.selectFollowUpDetail(followUpId);
    }

    /**
     * 新增随访对象
     * 
     * @param followUp 随访对象
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFollowUp(GcFollowUp followUp) {
        // 1. 验证必填字段
        validateFollowUp(followUp);

        // 2. 检查是否已存在随访对象
        if (followUp.getResultId() != null) {
            GcFollowUp existFollowUp = followUpMapper.selectByResultId(followUp.getResultId());
            if (existFollowUp != null) {
                throw new ServiceException("该筛查结果已存在随访对象");
            }
        }

        // 3. 设置默认值
        if (followUp.getFollowUpStatus() == null || followUp.getFollowUpStatus().trim().isEmpty()) {
            followUp.setFollowUpStatus("0"); // 待随访
        }
        if (followUp.getPlannedVisitCount() == null) {
            followUp.setPlannedVisitCount(3); // 默认计划随访3次
        }
        if (followUp.getActualVisitCount() == null) {
            followUp.setActualVisitCount(0); // 实际随访0次
        }
        if (followUp.getStartDate() == null) {
            followUp.setStartDate(new Date());
        }

        // 4. 插入随访对象
        return followUpMapper.insertFollowUp(followUp);
    }

    /**
     * 修改随访对象
     * 
     * @param followUp 随访对象
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFollowUp(GcFollowUp followUp) {
        // 1. 验证随访ID
        if (followUp.getFollowUpId() == null) {
            throw new ServiceException("随访ID不能为空");
        }

        // 2. 查询原随访对象
        Map<String, Object> oldFollowUp = followUpMapper.selectFollowUpDetail(followUp.getFollowUpId());
        if (oldFollowUp == null) {
            throw new ServiceException("随访对象不存在");
        }

        // 3. 仅允许修改待随访和随访中状态的对象
        String status = (String) oldFollowUp.get("follow_up_status");
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new ServiceException("已完成的随访对象不允许修改");
        }

        // 4. 更新随访对象
        return followUpMapper.updateFollowUp(followUp);
    }

    /**
     * 删除随访对象
     * 
     * @param followUpId 随访ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFollowUpById(Long followUpId) {
        // 1. 验证随访ID
        if (followUpId == null) {
            throw new ServiceException("随访ID不能为空");
        }

        // 2. 查询随访对象
        Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(followUpId);
        if (followUp == null) {
            throw new ServiceException("随访对象不存在");
        }

        // 3. 仅允许删除待随访状态的对象
        String status = (String) followUp.get("follow_up_status");
        if (!"0".equals(status)) {
            throw new ServiceException("仅允许删除待随访状态的对象");
        }

        // 4. 检查是否有随访跟踪记录
        int trackCount = followUpTrackMapper.countByFollowUpId(followUpId);
        if (trackCount > 0) {
            throw new ServiceException("存在随访跟踪记录，不允许删除");
        }

        // 5. 删除随访对象
        return followUpMapper.deleteFollowUpById(followUpId);
    }

    /**
     * 批量删除随访对象
     * 
     * @param followUpIds 随访ID数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFollowUpByIds(Long[] followUpIds) {
        if (followUpIds == null || followUpIds.length == 0) {
            throw new ServiceException("随访ID不能为空");
        }

        // 逐个验证并删除
        for (Long followUpId : followUpIds) {
            Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(followUpId);
            if (followUp != null) {
                String status = (String) followUp.get("follow_up_status");
                if (!"0".equals(status)) {
                    throw new ServiceException("存在非待随访状态的对象，不允许批量删除");
                }
                
                int trackCount = followUpTrackMapper.countByFollowUpId(followUpId);
                if (trackCount > 0) {
                    throw new ServiceException("存在随访跟踪记录，不允许批量删除");
                }
            }
        }

        return followUpMapper.deleteFollowUpByIds(followUpIds);
    }

    /**
     * 完成随访
     * 
     * @param followUpId 随访ID
     * @param followUpConclusion 随访结论
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeFollowUp(Long followUpId, String followUpConclusion) {
        // 1. 验证参数
        if (followUpId == null) {
            throw new ServiceException("随访ID不能为空");
        }

        // 2. 查询随访对象
        Map<String, Object> followUp = followUpMapper.selectFollowUpDetail(followUpId);
        if (followUp == null) {
            throw new ServiceException("随访对象不存在");
        }

        // 3. 检查状态
        String status = (String) followUp.get("follow_up_status");
        if ("2".equals(status)) {
            throw new ServiceException("该随访对象已完成，请勿重复操作");
        }

        // 4. 更新随访状态
        return followUpMapper.updateFollowUpStatus(
            followUpId, "2", new Date(), followUpConclusion
        );
    }

    /**
     * 根据居民ID查询随访对象列表
     * 
     * @param residentId 居民ID
     * @return 随访对象列表
     */
    @Override
    public List<Map<String, Object>> selectByResidentId(Long residentId) {
        if (residentId == null) {
            throw new ServiceException("居民ID不能为空");
        }
        return followUpMapper.selectByResidentId(residentId);
    }

    /**
     * 查询待随访列表
     * 
     * @return 待随访列表
     */
    @Override
    public List<Map<String, Object>> selectPendingFollowUps() {
        return followUpMapper.selectPendingFollowUps();
    }

    /**
     * 查询超期随访列表
     * 
     * @param days 超期天数
     * @return 超期随访列表
     */
    @Override
    public List<Map<String, Object>> selectOverdueFollowUps(int days) {
        if (days <= 0) {
            throw new ServiceException("超期天数必须大于0");
        }

        // 计算超期日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date overdueDate = calendar.getTime();

        return followUpMapper.selectOverdueFollowUps(overdueDate);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证随访对象必填字段
     */
    private void validateFollowUp(GcFollowUp followUp) {
        if (followUp.getResidentId() == null) {
            throw new ServiceException("居民ID不能为空");
        }
        if (followUp.getFollowUpReason() == null || followUp.getFollowUpReason().trim().isEmpty()) {
            throw new ServiceException("随访原因不能为空");
        }
    }
}
