package com.javaxiaobear.module.gc.domain;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 行政区划对象 gc_region
 * 
 * @author javaxiaobear
 * @date 2025-12-12
 */
public class GcRegion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 区域ID */
    private Long regionId;

    /** 区域名称 */
    private String regionName;

    /** 行政区划代码(12位国标代码) */
    private String regionCode;

    /** 区域层级(1省/2市/3区/4街道/5社区) */
    private Integer regionLevel;

    /** 父级区域ID */
    private Long parentId;

    /** 状态(0正常/1停用) */
    private String status;

    /** 排序号 */
    private Integer sortOrder;

    /** 父级区域名称 */
    private String parentName;

    public void setRegionId(Long regionId) 
    {
        this.regionId = regionId;
    }

    public Long getRegionId() 
    {
        return regionId;
    }
    public void setRegionName(String regionName) 
    {
        this.regionName = regionName;
    }

    public String getRegionName() 
    {
        return regionName;
    }
    public void setRegionCode(String regionCode) 
    {
        this.regionCode = regionCode;
    }

    public String getRegionCode() 
    {
        return regionCode;
    }
    public void setRegionLevel(Integer regionLevel) 
    {
        this.regionLevel = regionLevel;
    }

    public Integer getRegionLevel() 
    {
        return regionLevel;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }
    public void setParentName(String parentName) 
    {
        this.parentName = parentName;
    }

    public String getParentName() 
    {
        return parentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("regionId", getRegionId())
            .append("regionName", getRegionName())
            .append("regionCode", getRegionCode())
            .append("regionLevel", getRegionLevel())
            .append("parentId", getParentId())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}