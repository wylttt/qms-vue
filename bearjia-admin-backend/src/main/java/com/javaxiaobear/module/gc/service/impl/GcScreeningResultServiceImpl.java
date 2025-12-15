package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningStatisticsVO;
import com.javaxiaobear.module.gc.mapper.GcScreeningResultMapper;
import com.javaxiaobear.module.gc.service.IGcFollowUpService;
import com.javaxiaobear.module.gc.service.IGcScreeningResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 筛查结果Service业务层处理
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcScreeningResultServiceImpl implements IGcScreeningResultService {
    @Autowired
    private GcScreeningResultMapper screeningResultMapper;

    @Autowired
    private IGcFollowUpService followUpService;

    /**
     * 查询筛查结果列表
     *
     * @param screeningResult 筛查结果
     * @return 筛查结果
     */
    @Override
    public List<GcScreeningResult> selectScreeningResultList(GcScreeningResult screeningResult) {
        return screeningResultMapper.selectScreeningResultList(screeningResult);
    }

    /**
     * 查询筛查结果详情
     *
     * @param resultId 筛查结果主键
     * @return 筛查结果
     */
    @Override
    public GcScreeningResult selectScreeningResultById(Long resultId) {
        return screeningResultMapper.selectScreeningResultById(resultId);
    }

    /**
     * 根据居民ID查询筛查结果
     *
     * @param residentId 居民ID
     * @return 筛查结果
     */
    @Override
    public GcScreeningResult selectScreeningResultByResidentId(Long residentId) {
        return screeningResultMapper.selectScreeningResultByResidentId(residentId);
    }

    /**
     * 新增筛查结果
     *
     * @param screeningResult 筛查结果
     * @return 结果
     */
    @Override
    @Transactional
    public int insertScreeningResult(GcScreeningResult screeningResult) {
        int rows = screeningResultMapper.insertScreeningResult(screeningResult);
        
        // 如果是高风险结果，自动加入随访对象
        if (rows > 0 && "high_risk".equals(screeningResult.getBloodResult())) {
            followUpService.autoAddHighRiskFollowUp(screeningResult.getResidentId(), screeningResult.getResultId());
        }
        
        return rows;
    }

    /**
     * 修改筛查结果
     *
     * @param screeningResult 筛查结果
     * @return 结果
     */
    @Override
    @Transactional
    public int updateScreeningResult(GcScreeningResult screeningResult) {
        // 先查询原结果
        GcScreeningResult oldResult = screeningResultMapper.selectScreeningResultById(screeningResult.getResultId());
        
        int rows = screeningResultMapper.updateScreeningResult(screeningResult);
        
        // 如果结果从非高风险变为高风险，自动加入随访对象
        if (rows > 0 && !"high_risk".equals(oldResult.getBloodResult()) 
                && "high_risk".equals(screeningResult.getBloodResult())) {
            followUpService.autoAddHighRiskFollowUp(screeningResult.getResidentId(), screeningResult.getResultId());
        }
        
        return rows;
    }

    /**
     * 批量删除筛查结果
     *
     * @param resultIds 需要删除的筛查结果主键
     * @return 结果
     */
    @Override
    public int deleteScreeningResultByIds(Long[] resultIds) {
        return screeningResultMapper.deleteScreeningResultByIds(resultIds);
    }

    /**
     * 删除筛查结果信息
     *
     * @param resultId 筛查结果主键
     * @return 结果
     */
    @Override
    public int deleteScreeningResultById(Long resultId) {
        return screeningResultMapper.deleteScreeningResultById(resultId);
    }

    /**
     * 按采血点统计筛查数据
     *
     * @param siteId 采血点ID
     * @return 统计结果列表
     */
    @Override
    public List<ScreeningStatisticsVO> selectStatisticsBySite(Long siteId) {
        return screeningResultMapper.selectStatisticsBySite(siteId);
    }

    /**
     * 按行政区划统计筛查数据
     *
     * @param districtId 行政区划ID
     * @return 统计结果列表
     */
    @Override
    public List<ScreeningStatisticsVO> selectStatisticsByDistrict(Long districtId) {
        return screeningResultMapper.selectStatisticsByDistrict(districtId);
    }
}
