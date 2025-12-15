package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 筛查结果Mapper接口
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Mapper
public interface GcScreeningResultMapper {
    
    /**
     * 查询筛查结果
     * 
     * @param resultId 筛查结果主键
     * @return 筛查结果
     */
    public GcScreeningResult selectResultById(Long resultId);
    
    /**
     * 查询筛查结果列表
     * 
     * @param result 筛查结果
     * @return 筛查结果集合
     */
    public List<GcScreeningResult> selectResultList(GcScreeningResult result);
    
    /**
     * 新增筛查结果
     * 
     * @param result 筛查结果
     * @return 结果
     */
    public int insertResult(GcScreeningResult result);
    
    /**
     * 修改筛查结果
     * 
     * @param result 筛查结果
     * @return 结果
     */
    public int updateResult(GcScreeningResult result);
    
    /**
     * 删除筛查结果
     * 
     * @param resultId 筛查结果主键
     * @return 结果
     */
    public int deleteResultById(Long resultId);
    
    /**
     * 批量删除筛查结果
     * 
     * @param resultIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteResultByIds(Long[] resultIds);
    
    /**
     * 根据居民ID查询筛查结果
     * 
     * @param residentId 居民ID
     * @return 筛查结果
     */
    public GcScreeningResult selectResultByResidentId(Long residentId);
    
    /**
     * 查询高风险人群列表
     * 
     * @param result 查询条件
     * @return 筛查结果集合
     */
    public List<GcScreeningResult> selectHighRiskList(GcScreeningResult result);
    
    /**
     * 按采血点统计筛查结果
     * 
     * @param regionId 区域ID(可选,用于按区域过滤)
     * @return 统计结果集合
     */
    public List<ScreeningStatisticsVO> selectStatisticsBySite(@Param("regionId") Long regionId);
}
