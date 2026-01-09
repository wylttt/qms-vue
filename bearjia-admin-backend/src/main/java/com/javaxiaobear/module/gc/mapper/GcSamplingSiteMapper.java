package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 采血点Mapper接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Mapper
public interface GcSamplingSiteMapper {
    
    /**
     * 查询采血点
     * 
     * @param siteId 采血点主键
     * @return 采血点
     */
    public GcSamplingSite selectGcSamplingSiteBySiteId(Long siteId);
    
    /**
     * 查询采血点列表
     * 
     * @param gcSamplingSite 采血点
     * @return 采血点集合
     */
    public List<GcSamplingSite> selectGcSamplingSiteList(GcSamplingSite gcSamplingSite);
    
    /**
     * 新增采血点
     * 
     * @param gcSamplingSite 采血点
     * @return 结果
     */
    public int insertGcSamplingSite(GcSamplingSite gcSamplingSite);
    
    /**
     * 修改采血点
     * 
     * @param gcSamplingSite 采血点
     * @return 结果
     */
    public int updateGcSamplingSite(GcSamplingSite gcSamplingSite);
    
    /**
     * 删除采血点
     * 
     * @param siteId 采血点主键
     * @return 结果
     */
    public int deleteGcSamplingSiteBySiteId(Long siteId);
    
    /**
     * 批量删除采血点
     * 
     * @param siteIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGcSamplingSiteBySiteIds(Long[] siteIds);
}
