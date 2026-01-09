package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import com.javaxiaobear.module.gc.mapper.GcSamplingSiteMapper;
import com.javaxiaobear.module.gc.service.IGcSamplingSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 采血点Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcSamplingSiteServiceImpl implements IGcSamplingSiteService {
    
    @Autowired
    private GcSamplingSiteMapper gcSamplingSiteMapper;
    
    /**
     * 查询采血点
     * 
     * @param siteId 采血点主键
     * @return 采血点
     */
    @Override
    public GcSamplingSite selectGcSamplingSiteBySiteId(Long siteId) {
        return gcSamplingSiteMapper.selectGcSamplingSiteBySiteId(siteId);
    }
    
    /**
     * 查询采血点列表
     * 
     * @param gcSamplingSite 采血点
     * @return 采血点
     */
    @Override
    public List<GcSamplingSite> selectGcSamplingSiteList(GcSamplingSite gcSamplingSite) {
        return gcSamplingSiteMapper.selectGcSamplingSiteList(gcSamplingSite);
    }
    
    /**
     * 新增采血点
     * 
     * @param gcSamplingSite 采血点
     * @return 结果
     */
    @Override
    public int insertGcSamplingSite(GcSamplingSite gcSamplingSite) {
        // 默认状态为正常
        if (gcSamplingSite.getStatus() == null) {
            gcSamplingSite.setStatus("0");
        }
        return gcSamplingSiteMapper.insertGcSamplingSite(gcSamplingSite);
    }
    
    /**
     * 修改采血点
     * 
     * @param gcSamplingSite 采血点
     * @return 结果
     */
    @Override
    public int updateGcSamplingSite(GcSamplingSite gcSamplingSite) {
        return gcSamplingSiteMapper.updateGcSamplingSite(gcSamplingSite);
    }
    
    /**
     * 批量删除采血点
     * 
     * @param siteIds 需要删除的采血点主键
     * @return 结果
     */
    @Override
    public int deleteGcSamplingSiteBySiteIds(Long[] siteIds) {
        return gcSamplingSiteMapper.deleteGcSamplingSiteBySiteIds(siteIds);
    }
    
    /**
     * 删除采血点信息
     * 
     * @param siteId 采血点主键
     * @return 结果
     */
    @Override
    public int deleteGcSamplingSiteBySiteId(Long siteId) {
        return gcSamplingSiteMapper.deleteGcSamplingSiteBySiteId(siteId);
    }
}
