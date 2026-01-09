package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.vo.RegionTreeVO;
import com.javaxiaobear.module.gc.mapper.GcRegionMapper;
import com.javaxiaobear.module.gc.service.IGcRegionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行政区划Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcRegionServiceImpl implements IGcRegionService {
    
    @Autowired
    private GcRegionMapper gcRegionMapper;
    
    /**
     * 查询行政区划
     * 
     * @param regionId 行政区划主键
     * @return 行政区划
     */
    @Override
    public GcRegion selectGcRegionByRegionId(Long regionId) {
        return gcRegionMapper.selectGcRegionByRegionId(regionId);
    }
    
    /**
     * 查询行政区划列表
     * 
     * @param gcRegion 行政区划
     * @return 行政区划
     */
    @Override
    public List<GcRegion> selectGcRegionList(GcRegion gcRegion) {
        return gcRegionMapper.selectGcRegionList(gcRegion);
    }
    
    /**
     * 查询省级列表
     * 
     * @return 省级区划列表
     */
    @Override
    public List<GcRegion> selectProvinces() {
        return gcRegionMapper.selectByLevel(1);
    }
    
    /**
     * 查询市级列表
     * 
     * @param provinceId 省份ID
     * @return 市级区划列表
     */
    @Override
    public List<GcRegion> selectCitiesByProvinceId(Long provinceId) {
        return gcRegionMapper.selectByParentId(provinceId);
    }
    
    /**
     * 查询区级列表
     * 
     * @param cityId 城市ID
     * @return 区级区划列表
     */
    @Override
    public List<GcRegion> selectDistrictsByCityId(Long cityId) {
        return gcRegionMapper.selectByParentId(cityId);
    }
    
    /**
     * 查询街道级列表
     * 
     * @param districtId 区县ID
     * @return 街道级区划列表
     */
    @Override
    public List<GcRegion> selectStreetsByDistrictId(Long districtId) {
        return gcRegionMapper.selectByParentId(districtId);
    }
    
    /**
     * 查询社区级列表
     * 
     * @param streetId 街道ID
     * @return 社区级区划列表
     */
    @Override
    public List<GcRegion> selectCommunitiesByStreetId(Long streetId) {
        return gcRegionMapper.selectByParentId(streetId);
    }
    
    /**
     * 构建区划树形结构
     * 
     * @param rootId 根节点ID（不传则从省级开始）
     * @param maxLevel 最大层级（默认5）
     * @return 区划树
     */
    @Override
    public List<RegionTreeVO> buildRegionTree(Long rootId, Integer maxLevel) {
        if (maxLevel == null || maxLevel <= 0) {
            maxLevel = 5;
        }
        
        List<GcRegion> allRegions;
        if (rootId == null) {
            // 从省级开始构建
            allRegions = gcRegionMapper.selectByLevel(1);
        } else {
            // 从指定节点开始
            GcRegion root = gcRegionMapper.selectGcRegionByRegionId(rootId);
            if (root == null) {
                return new ArrayList<>();
            }
            allRegions = new ArrayList<>();
            allRegions.add(root);
        }
        
        return allRegions.stream()
                .map(region -> buildTree(region, maxLevel))
                .collect(Collectors.toList());
    }
    
    /**
     * 递归构建树形结构
     * 
     * @param region 当前区域
     * @param maxLevel 最大层级
     * @return 树节点
     */
    private RegionTreeVO buildTree(GcRegion region, Integer maxLevel) {
        RegionTreeVO treeVO = new RegionTreeVO();
        BeanUtils.copyProperties(region, treeVO);
        
        // 如果未达到最大层级，继续查询子级
        if (region.getRegionLevel() < maxLevel) {
            List<GcRegion> children = gcRegionMapper.selectByParentId(region.getRegionId());
            if (children != null && !children.isEmpty()) {
                List<RegionTreeVO> childrenVO = children.stream()
                        .map(child -> buildTree(child, maxLevel))
                        .collect(Collectors.toList());
                treeVO.setChildren(childrenVO);
            }
        }
        
        return treeVO;
    }
    
    /**
     * 新增行政区划
     * 
     * @param gcRegion 行政区划
     * @return 结果
     */
    @Override
    public int insertGcRegion(GcRegion gcRegion) {
        // 验证区划代码唯一性
        if (!checkRegionCodeUnique(gcRegion.getRegionCode())) {
            throw new RuntimeException("区划代码已存在");
        }
        
        // 验证父级区域
        if (gcRegion.getParentId() != null) {
            GcRegion parent = gcRegionMapper.selectGcRegionByRegionId(gcRegion.getParentId());
            if (parent == null) {
                throw new RuntimeException("父级区域不存在");
            }
            // 验证层级关系
            if (parent.getRegionLevel() + 1 != gcRegion.getRegionLevel()) {
                throw new RuntimeException("区域层级关系不正确");
            }
        }
        
        // 默认状态为正常
        if (gcRegion.getStatus() == null) {
            gcRegion.setStatus("0");
        }
        
        return gcRegionMapper.insertGcRegion(gcRegion);
    }
    
    /**
     * 修改行政区划
     * 
     * @param gcRegion 行政区划
     * @return 结果
     */
    @Override
    public int updateGcRegion(GcRegion gcRegion) {
        return gcRegionMapper.updateGcRegion(gcRegion);
    }
    
    /**
     * 批量删除行政区划
     * 
     * @param regionIds 需要删除的行政区划主键
     * @return 结果
     */
    @Override
    public int deleteGcRegionByRegionIds(Long[] regionIds) {
        for (Long regionId : regionIds) {
            // 检查是否存在子级
            if (gcRegionMapper.hasChildByRegionId(regionId) > 0) {
                GcRegion region = gcRegionMapper.selectGcRegionByRegionId(regionId);
                throw new RuntimeException(String.format("区域【%s】存在下级区域，不允许删除", region.getRegionName()));
            }
        }
        return gcRegionMapper.deleteGcRegionByRegionIds(regionIds);
    }
    
    /**
     * 删除行政区划信息
     * 
     * @param regionId 行政区划主键
     * @return 结果
     */
    @Override
    public int deleteGcRegionByRegionId(Long regionId) {
        // 检查是否存在子级
        if (gcRegionMapper.hasChildByRegionId(regionId) > 0) {
            GcRegion region = gcRegionMapper.selectGcRegionByRegionId(regionId);
            throw new RuntimeException(String.format("区域【%s】存在下级区域，不允许删除", region.getRegionName()));
        }
        return gcRegionMapper.deleteGcRegionByRegionId(regionId);
    }
    
    /**
     * 检查区划代码是否唯一
     * 
     * @param regionCode 区划代码
     * @return 结果
     */
    @Override
    public boolean checkRegionCodeUnique(String regionCode) {
        return gcRegionMapper.checkRegionCodeUnique(regionCode) == 0;
    }
}
