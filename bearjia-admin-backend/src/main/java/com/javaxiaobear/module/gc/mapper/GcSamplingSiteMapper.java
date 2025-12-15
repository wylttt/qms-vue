package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.javaxiaobear.module.gc.domain.GcSamplingSite;

/**
 * 采血点Mapper接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface GcSamplingSiteMapper
{
    /**
     * 查询采血点列表
     * 
     * @param samplingSite 采血点
     * @return 采血点集合
     */
    public List<GcSamplingSite> selectSamplingSiteList(GcSamplingSite samplingSite);

    /**
     * 根据采血点ID查询信息
     * 
     * @param siteId 采血点ID
     * @return 采血点
     */
    public GcSamplingSite selectSamplingSiteById(Long siteId);

    /**
     * 查询采血点下拉列表
     * 
     * @param regionId 所属街道ID
     * @return 采血点列表
     */
    public List<GcSamplingSite> selectSamplingSiteOptions(@Param("regionId") Long regionId);

    /**
     * 检查采血点是否有居民预约
     * 
     * @param siteId 采血点ID
     * @return 结果
     */
    public int checkSiteExistResident(Long siteId);

    /**
     * 新增采血点
     * 
     * @param samplingSite 采血点
     * @return 结果
     */
    public int insertSamplingSite(GcSamplingSite samplingSite);

    /**
     * 修改采血点
     * 
     * @param samplingSite 采血点
     * @return 结果
     */
    public int updateSamplingSite(GcSamplingSite samplingSite);

    /**
     * 删除采血点
     * 
     * @param siteId 采血点ID
     * @return 结果
     */
    public int deleteSamplingSiteById(Long siteId);
}
