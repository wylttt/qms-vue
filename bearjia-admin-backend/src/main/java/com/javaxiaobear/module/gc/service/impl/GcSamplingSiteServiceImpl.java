package com.javaxiaobear.module.gc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.GcSamplingSite;
import com.javaxiaobear.module.gc.mapper.GcRegionMapper;
import com.javaxiaobear.module.gc.mapper.GcSamplingSiteMapper;
import com.javaxiaobear.module.gc.service.IGcSamplingSiteService;

/**
 * 采血点Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcSamplingSiteServiceImpl implements IGcSamplingSiteService
{
    @Autowired
    private GcSamplingSiteMapper samplingSiteMapper;

    @Autowired
    private GcRegionMapper regionMapper;

    /**
     * 查询采血点列表
     * 
     * @param samplingSite 采血点
     * @return 采血点集合
     */
    @Override
    public List<GcSamplingSite> selectSamplingSiteList(GcSamplingSite samplingSite)
    {
        return samplingSiteMapper.selectSamplingSiteList(samplingSite);
    }

    /**
     * 根据采血点ID查询信息
     * 
     * @param siteId 采血点ID
     * @return 采血点
     */
    @Override
    public GcSamplingSite selectSamplingSiteById(Long siteId)
    {
        return samplingSiteMapper.selectSamplingSiteById(siteId);
    }

    /**
     * 查询采血点下拉列表
     * 
     * @param regionId 所属街道ID
     * @return 采血点列表
     */
    @Override
    public List<GcSamplingSite> selectSamplingSiteOptions(Long regionId)
    {
        return samplingSiteMapper.selectSamplingSiteOptions(regionId);
    }

    /**
     * 检查采血点是否有居民预约
     * 
     * @param siteId 采血点ID
     * @return 结果
     */
    @Override
    public boolean checkSiteExistResident(Long siteId)
    {
        int result = samplingSiteMapper.checkSiteExistResident(siteId);
        return result > 0;
    }

    /**
     * 新增采血点
     * 
     * @param samplingSite 采血点
     * @return 结果
     */
    @Override
    public int insertSamplingSite(GcSamplingSite samplingSite)
    {
        // 验证所属区域是否存在且为街道级别
        if (samplingSite.getRegionId() != null)
        {
            GcRegion region = regionMapper.selectRegionById(samplingSite.getRegionId());
            if (region == null)
            {
                throw new RuntimeException("所属区域不存在");
            }
            if (region.getRegionLevel() != 4)
            {
                throw new RuntimeException("采血点必须归属于街道/乡镇级别区域");
            }
        }

        // 默认状态为正常
        if (StringUtils.isEmpty(samplingSite.getStatus()))
        {
            samplingSite.setStatus("0");
        }

        return samplingSiteMapper.insertSamplingSite(samplingSite);
    }

    /**
     * 修改采血点
     * 
     * @param samplingSite 采血点
     * @return 结果
     */
    @Override
    public int updateSamplingSite(GcSamplingSite samplingSite)
    {
        return samplingSiteMapper.updateSamplingSite(samplingSite);
    }

    /**
     * 删除采血点
     * 
     * @param siteId 采血点ID
     * @return 结果
     */
    @Override
    public int deleteSamplingSiteById(Long siteId)
    {
        return samplingSiteMapper.deleteSamplingSiteById(siteId);
    }
}
