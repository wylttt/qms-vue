package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import java.util.List;

/**
 * 采血点Service接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface IGcSamplingSiteService {
    
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
     * 批量删除采血点
     * 
     * @param siteIds 需要删除的采血点主键集合
     * @return 结果
     */
    public int deleteGcSamplingSiteBySiteIds(Long[] siteIds);
    
    /**
     * 删除采血点信息
     * 
     * @param siteId 采血点主键
     * @return 结果
     */
    public int deleteGcSamplingSiteBySiteId(Long siteId);
}
