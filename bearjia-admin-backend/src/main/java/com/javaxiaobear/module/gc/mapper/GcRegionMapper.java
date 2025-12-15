package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.javaxiaobear.module.gc.domain.GcRegion;

/**
 * 行政区划Mapper接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface GcRegionMapper
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
     * 根据父级ID查询下级列表
     * 
     * @param parentId 父级ID
     * @param regionLevel 区域层级
     * @return 下级列表
     */
    public List<GcRegion> selectRegionListByParentId(@Param("parentId") Long parentId, @Param("regionLevel") Integer regionLevel);

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
    public int hasChildByRegionId(Long regionId);

    /**
     * 检查区划代码是否唯一
     * 
     * @param regionCode 区划代码
     * @param regionId 区域ID
     * @return 结果
     */
    public GcRegion checkRegionCodeUnique(@Param("regionCode") String regionCode, @Param("regionId") Long regionId);

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
     * 检查区划是否被采血点引用
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    public int checkRegionExistSamplingSite(Long regionId);

    /**
     * 检查区划是否被调查员引用
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    public int checkRegionExistSurveyor(Long regionId);
}
