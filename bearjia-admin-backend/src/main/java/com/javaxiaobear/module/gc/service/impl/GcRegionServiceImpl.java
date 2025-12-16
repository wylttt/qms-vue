package com.javaxiaobear.module.gc.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;
import com.javaxiaobear.module.gc.mapper.GcRegionMapper;
import com.javaxiaobear.module.gc.service.IGcRegionService;

/**
 * 行政区划Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcRegionServiceImpl implements IGcRegionService
{
    @Autowired
    private GcRegionMapper regionMapper;

    /**
     * 查询行政区划列表
     * 
     * @param region 行政区划
     * @return 行政区划集合
     */
    @Override
    public List<GcRegion> selectRegionList(GcRegion region)
    {
        return regionMapper.selectRegionList(region);
    }

    /**
     * 查询省级列表
     * 
     * @return 省级列表
     */
    @Override
    public List<GcRegion> selectProvinceList()
    {
        return regionMapper.selectProvinceList();
    }

    /**
     * 查询城市列表
     * 
     * @param provinceId 省份ID
     * @return 城市列表
     */
    @Override
    public List<GcRegion> selectCityList(Long provinceId)
    {
        return regionMapper.selectRegionListByParentId(provinceId, 2);
    }

    /**
     * 查询区县列表
     * 
     * @param cityId 城市ID
     * @return 区县列表
     */
    @Override
    public List<GcRegion> selectDistrictList(Long cityId)
    {
        return regionMapper.selectRegionListByParentId(cityId, 3);
    }

    /**
     * 查询街道/乡镇列表
     * 
     * @param districtId 区县ID
     * @return 街道/乡镇列表
     */
    @Override
    public List<GcRegion> selectStreetList(Long districtId)
    {
        return regionMapper.selectRegionListByParentId(districtId, 4);
    }

    /**
     * 查询社区/村列表
     * 
     * @param streetId 街道/乡镇ID
     * @return 社区/村列表
     */
    @Override
    public List<GcRegion> selectCommunityList(Long streetId)
    {
        return regionMapper.selectRegionListByParentId(streetId, 5);
    }

    /**
     * 查询区划树形结构
     * 
     * @param rootId 根节点ID,不传则从省级开始
     * @param maxLevel 最大层级,默认5
     * @return 区划树形结构
     */
    @Override
    public List<RegionTreeVO> selectRegionTree(Long rootId, Integer maxLevel)
    {
        List<GcRegion> regionList = null;
        if (rootId == null)
        {
            // 从省级开始
            regionList = selectRegionList(new GcRegion());
        }
        else
        {
            // 从指定节点开始
            GcRegion rootRegion = selectRegionById(rootId);
            if (rootRegion != null)
            {
                regionList = new ArrayList<>();
                regionList.add(rootRegion);
                // 查询所有下级区划
                List<GcRegion> childList = selectChildRegions(rootId, maxLevel != null ? maxLevel : 5);
                regionList.addAll(childList);
            }
        }

        if (regionList == null || regionList.isEmpty())
        {
            return new ArrayList<>();
        }

        return buildRegionTree(regionList);
    }

    /**
     * 递归查询下级区划
     * 
     * @param parentId 父级ID
     * @param maxLevel 最大层级
     * @return 下级区划列表
     */
    private List<GcRegion> selectChildRegions(Long parentId, Integer maxLevel)
    {
        List<GcRegion> result = new ArrayList<>();
        GcRegion query = new GcRegion();
        query.setParentId(parentId);
        query.setStatus("0");
        List<GcRegion> children = regionMapper.selectRegionList(query);
        
        if (children != null && !children.isEmpty())
        {
            result.addAll(children);
            for (GcRegion child : children)
            {
                if (child.getRegionLevel() < maxLevel)
                {
                    List<GcRegion> grandChildren = selectChildRegions(child.getRegionId(), maxLevel);
                    result.addAll(grandChildren);
                }
            }
        }
        
        return result;
    }

    /**
     * 构建区划树形结构
     * 
     * @param regions 区划列表
     * @return 树形结构
     */
    @Override
    public List<RegionTreeVO> buildRegionTree(List<GcRegion> regions)
    {
        List<RegionTreeVO> returnList = new ArrayList<>();
        List<Long> tempList = regions.stream().map(GcRegion::getRegionId).collect(Collectors.toList());
        
        for (GcRegion region : regions)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(region.getParentId()))
            {
                recursionFn(regions, region, returnList);
            }
        }
        
        if (returnList.isEmpty())
        {
            returnList = regions.stream().map(this::convertToTreeVO).collect(Collectors.toList());
        }
        
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<GcRegion> list, GcRegion region, List<RegionTreeVO> returnList)
    {
        RegionTreeVO node = convertToTreeVO(region);
        
        // 得到子节点列表
        List<RegionTreeVO> childList = getChildList(list, region);
        node.setChildren(childList);
        returnList.add(node);
    }

    /**
     * 得到子节点列表
     */
    private List<RegionTreeVO> getChildList(List<GcRegion> list, GcRegion region)
    {
        List<RegionTreeVO> tlist = new ArrayList<>();
        Iterator<GcRegion> it = list.iterator();
        while (it.hasNext())
        {
            GcRegion n = it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == region.getRegionId().longValue())
            {
                recursionFn(list, n, tlist);
            }
        }
        return tlist;
    }

    /**
     * 转换为树形VO对象
     */
    private RegionTreeVO convertToTreeVO(GcRegion region)
    {
        RegionTreeVO vo = new RegionTreeVO();
        vo.setRegionId(region.getRegionId());
        vo.setRegionName(region.getRegionName());
        vo.setRegionCode(region.getRegionCode());
        vo.setRegionLevel(region.getRegionLevel());
        vo.setParentId(region.getParentId());
        return vo;
    }

    /**
     * 根据区域ID查询信息
     * 
     * @param regionId 区域ID
     * @return 行政区划
     */
    @Override
    public GcRegion selectRegionById(Long regionId)
    {
        return regionMapper.selectRegionById(regionId);
    }

    /**
     * 检查是否存在子级区划
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    @Override
    public boolean hasChildByRegionId(Long regionId)
    {
        int result = regionMapper.hasChildByRegionId(regionId);
        return result > 0;
    }

    /**
     * 检查区划代码是否唯一
     * 
     * @param region 区划信息
     * @return 结果
     */
    @Override
    public boolean checkRegionCodeUnique(GcRegion region)
    {
        Long regionId = StringUtils.isNull(region.getRegionId()) ? -1L : region.getRegionId();
        GcRegion info = regionMapper.checkRegionCodeUnique(region.getRegionCode(), regionId);
        if (StringUtils.isNotNull(info) && info.getRegionId().longValue() != regionId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 新增行政区划
     * 
     * @param region 行政区划
     * @return 结果
     */
    @Override
    public int insertRegion(GcRegion region)
    {
        // 验证父级区划是否存在
        if (region.getParentId() != null && region.getParentId() > 0)
        {
            GcRegion parentRegion = regionMapper.selectRegionById(region.getParentId());
            if (parentRegion == null)
            {
                throw new RuntimeException("父级区划不存在");
            }
            // 验证层级是否正确
            if (parentRegion.getRegionLevel() + 1 != region.getRegionLevel())
            {
                throw new RuntimeException("区划层级设置错误");
            }
        }
        
        // 默认状态为正常
        if (StringUtils.isEmpty(region.getStatus()))
        {
            region.setStatus("0");
        }
        
        return regionMapper.insertRegion(region);
    }

    /**
     * 修改行政区划
     * 
     * @param region 行政区划
     * @return 结果
     */
    @Override
    public int updateRegion(GcRegion region)
    {
        return regionMapper.updateRegion(region);
    }

    /**
     * 删除行政区划
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    @Override
    public int deleteRegionById(Long regionId)
    {
        return regionMapper.deleteRegionById(regionId);
    }

    /**
     * 检查区划是否被引用
     * 
     * @param regionId 区域ID
     * @return 结果
     */
    @Override
    public boolean checkRegionInUse(Long regionId)
    {
        int samplingSiteCount = regionMapper.checkRegionExistSamplingSite(regionId);
        if (samplingSiteCount > 0)
        {
            return true;
        }
        
        int surveyorCount = regionMapper.checkRegionExistSurveyor(regionId);
        if (surveyorCount > 0)
        {
            return true;
        }
        
        return false;
    }
}
