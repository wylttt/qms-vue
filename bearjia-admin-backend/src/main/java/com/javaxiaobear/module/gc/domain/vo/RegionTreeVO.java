package com.javaxiaobear.module.gc.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区划树形结构视图对象
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public class RegionTreeVO
{
    /** 区域ID */
    private Long regionId;

    /** 区域名称 */
    private String regionName;

    /** 行政区划代码 */
    private String regionCode;

    /** 区域层级(1省/2市/3区/4街道/5社区) */
    private Integer regionLevel;

    /** 父级ID */
    private Long parentId;

    /** 子级列表 */
    private List<RegionTreeVO> children = new ArrayList<>();

    public Long getRegionId()
    {
        return regionId;
    }

    public void setRegionId(Long regionId)
    {
        this.regionId = regionId;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Integer getRegionLevel()
    {
        return regionLevel;
    }

    public void setRegionLevel(Integer regionLevel)
    {
        this.regionLevel = regionLevel;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public List<RegionTreeVO> getChildren()
    {
        return children;
    }

    public void setChildren(List<RegionTreeVO> children)
    {
        this.children = children;
    }
}
