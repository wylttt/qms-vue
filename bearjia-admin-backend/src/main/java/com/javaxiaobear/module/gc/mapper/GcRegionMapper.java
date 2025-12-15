package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcRegion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行政区划Mapper接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Mapper
public interface GcRegionMapper {
    
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
     * 根据层级查询区划列表
     * 
     * @param regionLevel 区域层级
     * @return 区划列表
     */
    public List<GcRegion> selectByLevel(@Param("regionLevel") Integer regionLevel);
    
    /**
     * 根据父级ID查询子级区划列表
     * 
     * @param parentId 父级ID
     * @return 子级区划列表
     */
    public List<GcRegion> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据区划代码查询
     * 
     * @param regionCode 区划代码
     * @return 行政区划
     */
    public GcRegion selectByRegionCode(@Param("regionCode") String regionCode);
    
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
     * 删除行政区划
     * 
     * @param regionId 行政区划主键
     * @return 结果
     */
    public int deleteGcRegionByRegionId(Long regionId);
    
    /**
     * 批量删除行政区划
     * 
     * @param regionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGcRegionByRegionIds(Long[] regionIds);
    
    /**
     * 检查区划代码是否唯一
     * 
     * @param regionCode 区划代码
     * @return 数量
     */
    public int checkRegionCodeUnique(@Param("regionCode") String regionCode);
    
    /**
     * 检查是否存在子级区划
     * 
     * @param regionId 区域ID
     * @return 子级数量
     */
    public int hasChildByRegionId(@Param("regionId") Long regionId);
}
