package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.domain.entity.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningResultVO;
import com.javaxiaobear.module.gc.mapper.GcFollowUpMapper;
import com.javaxiaobear.module.gc.mapper.GcScreeningResultMapper;
import com.javaxiaobear.module.gc.service.IGcScreeningResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛查结果Service实现类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcScreeningResultServiceImpl implements IGcScreeningResultService {

    @Autowired
    private GcScreeningResultMapper screeningResultMapper;

    @Autowired
    private GcFollowUpMapper followUpMapper;

    /**
     * 查询筛查结果列表
     * 
     * @param result 查询条件
     * @return 筛查结果列表
     */
    @Override
    public List<ScreeningResultVO> selectScreeningResultList(GcScreeningResult result) {
        return screeningResultMapper.selectScreeningResultList(result);
    }

    /**
     * 查询筛查结果详情
     * 
     * @param resultId 结果ID
     * @return 筛查结果详情
     */
    @Override
    public ScreeningResultVO selectScreeningResultDetail(Long resultId) {
        if (resultId == null) {
            throw new ServiceException("结果ID不能为空");
        }
        return screeningResultMapper.selectScreeningResultDetail(resultId);
    }

    /**
     * 新增筛查结果
     * 
     * @param result 筛查结果
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertScreeningResult(GcScreeningResult result) {
        // 1. 验证必填字段
        validateScreeningResult(result);

        // 2. 检查是否已存在筛查结果
        if (result.getAppointmentId() != null) {
            GcScreeningResult existResult = screeningResultMapper.selectByAppointmentId(result.getAppointmentId());
            if (existResult != null) {
                throw new ServiceException("该预约已存在筛查结果");
            }
        }

        // 3. 计算风险等级
        calculateRiskLevel(result);

        // 4. 设置默认值
        result.setReviewStatus("0"); // 待审核
        if (result.getNeedFollowUp() == null) {
            result.setNeedFollowUp(0);
        }

        // 5. 插入筛查结果
        int rows = screeningResultMapper.insertScreeningResult(result);

        // 6. 如果需要随访,自动创建随访对象
        if (rows > 0 && result.getNeedFollowUp() == 1) {
            createFollowUp(result);
        }

        return rows;
    }

    /**
     * 修改筛查结果
     * 
     * @param result 筛查结果
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateScreeningResult(GcScreeningResult result) {
        // 1. 验证结果ID
        if (result.getResultId() == null) {
            throw new ServiceException("结果ID不能为空");
        }

        // 2. 查询原结果信息
        ScreeningResultVO oldResult = screeningResultMapper.selectScreeningResultDetail(result.getResultId());
        if (oldResult == null) {
            throw new ServiceException("筛查结果不存在");
        }

        // 3. 仅允许修改待审核状态的结果
        if (!"0".equals(oldResult.getReviewStatus())) {
            throw new ServiceException("已审核的结果不允许修改");
        }

        // 4. 重新计算风险等级
        calculateRiskLevel(result);

        // 5. 更新筛查结果
        int rows = screeningResultMapper.updateScreeningResult(result);

        // 6. 如果需要随访标记发生变化,处理随访对象
        if (rows > 0) {
            if (result.getNeedFollowUp() != null && result.getNeedFollowUp() == 1 
                    && oldResult.getNeedFollowUp() == 0) {
                // 从不需要变为需要,创建随访对象
                createFollowUp(result);
            } else if (result.getNeedFollowUp() != null && result.getNeedFollowUp() == 0 
                    && oldResult.getNeedFollowUp() == 1) {
                // 从需要变为不需要,删除随访对象
                GcFollowUp followUp = followUpMapper.selectByResultId(result.getResultId());
                if (followUp != null && "0".equals(followUp.getFollowUpStatus())) {
                    // 仅删除待随访状态的随访对象
                    followUpMapper.deleteFollowUpById(followUp.getFollowUpId());
                }
            }
        }

        return rows;
    }

    /**
     * 删除筛查结果
     * 
     * @param resultId 结果ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteScreeningResultById(Long resultId) {
        // 1. 验证结果ID
        if (resultId == null) {
            throw new ServiceException("结果ID不能为空");
        }

        // 2. 查询筛查结果
        ScreeningResultVO result = screeningResultMapper.selectScreeningResultDetail(resultId);
        if (result == null) {
            throw new ServiceException("筛查结果不存在");
        }

        // 3. 仅允许删除待审核状态的结果
        if (!"0".equals(result.getReviewStatus())) {
            throw new ServiceException("仅允许删除待审核状态的结果");
        }

        // 4. 删除关联的随访对象(如果存在且为待随访状态)
        GcFollowUp followUp = followUpMapper.selectByResultId(resultId);
        if (followUp != null && "0".equals(followUp.getFollowUpStatus())) {
            followUpMapper.deleteFollowUpById(followUp.getFollowUpId());
        }

        // 5. 删除筛查结果
        return screeningResultMapper.deleteScreeningResultById(resultId);
    }

    /**
     * 批量删除筛查结果
     * 
     * @param resultIds 结果ID数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteScreeningResultByIds(Long[] resultIds) {
        if (resultIds == null || resultIds.length == 0) {
            throw new ServiceException("结果ID不能为空");
        }

        // 逐个验证并删除
        for (Long resultId : resultIds) {
            ScreeningResultVO result = screeningResultMapper.selectScreeningResultDetail(resultId);
            if (result != null && !"0".equals(result.getReviewStatus())) {
                throw new ServiceException("存在已审核的结果,不允许批量删除");
            }
        }

        return screeningResultMapper.deleteScreeningResultByIds(resultIds);
    }

    /**
     * 审核筛查结果
     * 
     * @param resultId 结果ID
     * @param reviewUserId 审核人ID
     * @param reviewUserName 审核人姓名
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reviewScreeningResult(Long resultId, Long reviewUserId, String reviewUserName) {
        // 1. 验证参数
        if (resultId == null) {
            throw new ServiceException("结果ID不能为空");
        }
        if (reviewUserId == null || reviewUserName == null || reviewUserName.trim().isEmpty()) {
            throw new ServiceException("审核人信息不能为空");
        }

        // 2. 查询筛查结果
        ScreeningResultVO result = screeningResultMapper.selectScreeningResultDetail(resultId);
        if (result == null) {
            throw new ServiceException("筛查结果不存在");
        }

        // 3. 检查审核状态
        if ("1".equals(result.getReviewStatus())) {
            throw new ServiceException("该结果已审核,请勿重复操作");
        }

        // 4. 更新审核状态
        return screeningResultMapper.updateReviewStatus(
            resultId, "1", reviewUserId, reviewUserName, new Date()
        );
    }

    /**
     * 根据居民ID查询筛查结果列表
     * 
     * @param residentId 居民ID
     * @return 筛查结果列表
     */
    @Override
    public List<ScreeningResultVO> selectByResidentId(Long residentId) {
        if (residentId == null) {
            throw new ServiceException("居民ID不能为空");
        }
        return screeningResultMapper.selectByResidentId(residentId);
    }

    /**
     * 统计各风险等级人数
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    @Override
    public Map<String, Object> countByRiskLevel(Long regionId, Integer regionLevel, Date startDate, Date endDate) {
        List<Map<String, Object>> list = screeningResultMapper.countByRiskLevel(
            regionId, regionLevel, startDate, endDate
        );

        Map<String, Object> result = new HashMap<>();
        result.put("lowRisk", 0);
        result.put("mediumRisk", 0);
        result.put("highRisk", 0);
        result.put("total", 0);

        int total = 0;
        for (Map<String, Object> item : list) {
            String riskLevel = (String) item.get("risk_level");
            Long count = (Long) item.get("count");
            total += count.intValue();

            if ("1".equals(riskLevel)) {
                result.put("lowRisk", count.intValue());
            } else if ("2".equals(riskLevel)) {
                result.put("mediumRisk", count.intValue());
            } else if ("3".equals(riskLevel)) {
                result.put("highRisk", count.intValue());
            }
        }
        result.put("total", total);

        return result;
    }

    /**
     * 导出筛查结果列表
     * 
     * @param result 查询条件
     * @return 筛查结果列表
     */
    @Override
    public List<ScreeningResultVO> exportScreeningResultList(GcScreeningResult result) {
        return screeningResultMapper.selectScreeningResultList(result);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证筛查结果必填字段
     */
    private void validateScreeningResult(GcScreeningResult result) {
        if (result.getResidentId() == null) {
            throw new ServiceException("居民ID不能为空");
        }
        if (result.getScreeningType() == null || result.getScreeningType().trim().isEmpty()) {
            throw new ServiceException("筛查类型不能为空");
        }
    }

    /**
     * 计算风险等级
     */
    private void calculateRiskLevel(GcScreeningResult result) {
        // 血液筛查结果:0-未检测/1-中低风险/2-高风险
        // 风险等级:1-低风险/2-中风险/3-高风险

        if ("1".equals(result.getScreeningType())) {
            // 血液筛查
            if ("2".equals(result.getBloodResult())) {
                // 高风险
                result.setRiskLevel("3");
                result.setNeedFollowUp(1);
            } else if ("1".equals(result.getBloodResult())) {
                // 中低风险
                result.setRiskLevel("2");
                result.setNeedFollowUp(0);
            } else {
                // 未检测或正常
                result.setRiskLevel("1");
                result.setNeedFollowUp(0);
            }
        }
        // TODO: 胃镜筛查风险等级计算(预留)
    }

    /**
     * 创建随访对象
     */
    private void createFollowUp(GcScreeningResult result) {
        GcFollowUp followUp = new GcFollowUp();
        followUp.setResidentId(result.getResidentId());
        followUp.setResultId(result.getResultId());
        followUp.setFollowUpReason("1"); // 高风险人群
        followUp.setFollowUpStatus("0"); // 待随访
        followUp.setPlannedVisitCount(3); // 计划随访3次
        followUp.setActualVisitCount(0); // 实际随访0次
        followUp.setStartDate(new Date());

        followUpMapper.insertFollowUp(followUp);
    }
}
