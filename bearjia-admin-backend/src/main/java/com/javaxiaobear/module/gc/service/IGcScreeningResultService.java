package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningStatisticsVO;

import java.util.List;

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
     * @param screeningResult 筛查结果
     * @return 筛查结果集合
     */
    List<GcScreeningResult> selectScreeningResultList(GcScreeningResult screeningResult);

    /**
     * 查询筛查结果详情
     *
     * @param resultId 筛查结果主键
     * @return 筛查结果
     */
    GcScreeningResult selectScreeningResultById(Long resultId);

    /**
     * 根据居民ID查询筛查结果
     *
     * @param residentId 居民ID
     * @return 筛查结果
     */
    GcScreeningResult selectScreeningResultByResidentId(Long residentId);

    /**
     * 新增筛查结果
     *
     * @param screeningResult 筛查结果
     * @return 结果
     */
    int insertScreeningResult(GcScreeningResult screeningResult);

    /**
     * 修改筛查结果
     *
     * @param screeningResult 筛查结果
     * @return 结果
     */
    int updateScreeningResult(GcScreeningResult screeningResult);

    /**
     * 批量删除筛查结果
     *
     * @param resultIds 需要删除的筛查结果主键集合
     * @return 结果
     */
    int deleteScreeningResultByIds(Long[] resultIds);

    /**
     * 删除筛查结果信息
     *
     * @param resultId 筛查结果主键
     * @return 结果
     */
    int deleteScreeningResultById(Long resultId);

    /**
     * 按采血点统计筛查数据
     *
     * @param siteId 采血点ID（可选，不传则统计所有采血点）
     * @return 统计结果列表
     */
    List<ScreeningStatisticsVO> selectStatisticsBySite(Long siteId);

    /**
     * 按行政区划统计筛查数据
     *
     * @param districtId 行政区划ID（可选，不传则统计所有区划）
     * @return 统计结果列表
     */
    List<ScreeningStatisticsVO> selectStatisticsByDistrict(Long districtId);
}
