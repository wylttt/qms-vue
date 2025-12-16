package com.javaxiaobear.module.gc.service;

import java.util.List;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;

/**
 * 行政区划Service接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface IGcRegionService
{
    /**
     * 查询行政区划列表
     * 
     * @param region 行政区划
     * @return 行政区划集合
     */
    public List<GcRegion> selectRegionList(GcRegion region);

    /**
     * 查询省级列表
     * 
     * @return 省级列表
     */
    public List<GcRegion> selectProvinceList();

    /**
     * 查询城市列表
     * 
     * @param provinceId 省份ID
     * @return 城市列表
     */
    public List<GcRegion> selectCityList(Long provinceId);

    /**
     * 查询区县列表
     * 
     * @param cityId 城市ID
     * @return 区县列表
     */
    public List<GcRegion> selectDistrictList(Long cityId);

    /**
     * 查询街道/乡镇列表
     * 
     * @param districtId 区县ID
     * @return 街道/乡镇列表
     */
    public List<GcRegion> selectStreetList(Long districtId);

    /**
     * 查询社区/村列表
     * 
     * @param streetId 街道/乡镇ID
     * @return 社区/村列表
     */
    public List<GcRegion> selectCommunityList(Long streetId);

    /**
     * 查询区划树形结构
     * 
     * @param rootId 根节点ID,不传则从省级开始
     * @param maxLevel 最大层级,默认5
     * @return 区划树形结构
     */
    public List<RegionTreeVO> selectRegionTree(Long rootId, Integer maxLevel);

    /**
     * 构建区划树形结构
     * 
     * @param regions 区划列表
     * @return 树形结构
     */
    public List<RegionTreeVO> buildRegionTree(List<GcRegion> regions);

    /**
     * 根据区域ID查询信息
     * 
     * @param regionId 区域ID
     * @return 行政区划
     */
    public GcRegion selectRegionById(Long regionId);

    /**
     * 检查是否存在子级区划
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    public boolean hasChildByRegionId(Long regionId);

    /**
     * 检查区划代码是否唯一
     * 
     * @param region 区划信息
     * @return 结果
     */
    public boolean checkRegionCodeUnique(GcRegion region);

    /**
     * 新增行政区划
     * 
     * @param region 行政区划
     * @return 结果
     */
    public int insertRegion(GcRegion region);

    /**
     * 修改行政区划
     * 
     * @param region 行政区划
     * @return 结果
     */
    public int updateRegion(GcRegion region);

    /**
     * 删除行政区划
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    public int deleteRegionById(Long regionId);

    /**
     * 检查区划是否被引用
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    public boolean checkRegionInUse(Long regionId);
}
