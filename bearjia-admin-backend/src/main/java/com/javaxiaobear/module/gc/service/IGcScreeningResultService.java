package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningResultVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 筛查结果Service接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
public interface IGcScreeningResultService {

    /**
     * 查询筛查结果列表
     * 
     * @param result 查询条件
     * @return 筛查结果列表
     */
    List<ScreeningResultVO> selectScreeningResultList(GcScreeningResult result);

    /**
     * 查询筛查结果详情
     * 
     * @param resultId 结果ID
     * @return 筛查结果详情
     */
    ScreeningResultVO selectScreeningResultDetail(Long resultId);

    /**
     * 新增筛查结果
     * 
     * @param result 筛查结果
     * @return 新增结果
     */
    int insertScreeningResult(GcScreeningResult result);

    /**
     * 修改筛查结果
     * 
     * @param result 筛查结果
     * @return 修改结果
     */
    int updateScreeningResult(GcScreeningResult result);

    /**
     * 删除筛查结果
     * 
     * @param resultId 结果ID
     * @return 删除结果
     */
    int deleteScreeningResultById(Long resultId);

    /**
     * 批量删除筛查结果
     * 
     * @param resultIds 结果ID数组
     * @return 删除结果
     */
    int deleteScreeningResultByIds(Long[] resultIds);

    /**
     * 审核筛查结果
     * 
     * @param resultId 结果ID
     * @param reviewUserId 审核人ID
     * @param reviewUserName 审核人姓名
     * @return 更新结果
     */
    int reviewScreeningResult(Long resultId, Long reviewUserId, String reviewUserName);

    /**
     * 根据居民ID查询筛查结果列表
     * 
     * @param residentId 居民ID
     * @return 筛查结果列表
     */
    List<ScreeningResultVO> selectByResidentId(Long residentId);

    /**
     * 统计各风险等级人数
     * 
     * @param regionId 区域ID
     * @param regionLevel 区域层级
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    Map<String, Object> countByRiskLevel(Long regionId, Integer regionLevel, Date startDate, Date endDate);

    /**
     * 导出筛查结果列表
     * 
     * @param result 查询条件
     * @return 筛查结果列表
     */
    List<ScreeningResultVO> exportScreeningResultList(GcScreeningResult result);
}
