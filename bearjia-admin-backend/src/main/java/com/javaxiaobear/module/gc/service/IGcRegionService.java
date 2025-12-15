package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;

import java.util.List;

/**
 * 行政区划Service接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface IGcRegionService {
    
    /**
     * 查询行政区划
     * 
     * @param regionId 行政区划主键
     * @return 行政区划
     */
    public GcRegion selectGcRegionByRegionId(Long regionId);
    
    /**
     * 查询行政区划列表
     * 
     * @param gcRegion 行政区划
     * @return 行政区划集合
     */
    public List<GcRegion> selectGcRegionList(GcRegion gcRegion);
    
    /**
     * 查询省级列表
     * 
     * @return 省级区划列表
     */
    public List<GcRegion> selectProvinces();
    
    /**
     * 查询市级列表
     * 
     * @param provinceId 省份ID
     * @return 市级区划列表
     */
    public List<GcRegion> selectCitiesByProvinceId(Long provinceId);
    
    /**
     * 查询区级列表
     * 
     * @param cityId 城市ID
     * @return 区级区划列表
     */
    public List<GcRegion> selectDistrictsByCityId(Long cityId);
    
    /**
     * 查询街道级列表
     * 
     * @param districtId 区县ID
     * @return 街道级区划列表
     */
    public List<GcRegion> selectStreetsByDistrictId(Long districtId);
    
    /**
     * 查询社区级列表
     * 
     * @param streetId 街道ID
     * @return 社区级区划列表
     */
    public List<GcRegion> selectCommunitiesByStreetId(Long streetId);
    
    /**
     * 构建区划树形结构
     * 
     * @param rootId 根节点ID（不传则从省级开始）
     * @param maxLevel 最大层级（默认5）
     * @return 区划树
     */
    public List<RegionTreeVO> buildRegionTree(Long rootId, Integer maxLevel);
    
    /**
     * 新增行政区划
     * 
     * @param gcRegion 行政区划
     * @return 结果
     */
    public int insertGcRegion(GcRegion gcRegion);
    
    /**
     * 修改行政区划
     * 
     * @param gcRegion 行政区划
     * @return 结果
     */
    public int updateGcRegion(GcRegion gcRegion);
    
    /**
     * 批量删除行政区划
     * 
     * @param regionIds 需要删除的行政区划主键集合
     * @return 结果
     */
    public int deleteGcRegionByRegionIds(Long[] regionIds);
    
    /**
     * 删除行政区划信息
     * 
     * @param regionId 行政区划主键
     * @return 结果
     */
    public int deleteGcRegionByRegionId(Long regionId);
    
    /**
     * 检查区划代码是否唯一
     * 
     * @param regionCode 区划代码
     * @return 结果
     */
    public boolean checkRegionCodeUnique(String regionCode);
}
