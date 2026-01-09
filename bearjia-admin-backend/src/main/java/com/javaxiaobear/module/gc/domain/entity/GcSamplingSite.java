package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 采血点对象 gc_sampling_site
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public class GcSamplingSite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 采血点ID */
    private Long siteId;

    /** 采血点名称 */
    private String siteName;

    /** 所属街道/乡镇ID */
    private Long regionId;

    /** 详细地址 */
    private String address;

    /** 街道管理员ID */
    private Long streetManagerId;

    /** 街道管理员姓名 */
    private String streetManagerName;

    /** 街道管理员电话 */
    private String streetManagerPhone;

    /** 采血点管理员ID */
    private Long siteManagerId;

    /** 采血点管理员姓名 */
    private String siteManagerName;

    /** 采血点管理员电话 */
    private String siteManagerPhone;

    /** 状态(0正常/1停用) */
    private String status;

    /** 每日最大预约容量 */
    private Integer dailyCapacity;

    /** 区域名称(关联查询字段) */
    private String regionName;

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setStreetManagerId(Long streetManagerId) {
        this.streetManagerId = streetManagerId;
    }

    public Long getStreetManagerId() {
        return streetManagerId;
    }

    public void setStreetManagerName(String streetManagerName) {
        this.streetManagerName = streetManagerName;
    }

    public String getStreetManagerName() {
        return streetManagerName;
    }

    public void setStreetManagerPhone(String streetManagerPhone) {
        this.streetManagerPhone = streetManagerPhone;
    }

    public String getStreetManagerPhone() {
        return streetManagerPhone;
    }

    public void setSiteManagerId(Long siteManagerId) {
        this.siteManagerId = siteManagerId;
    }

    public Long getSiteManagerId() {
        return siteManagerId;
    }

    public void setSiteManagerName(String siteManagerName) {
        this.siteManagerName = siteManagerName;
    }

    public String getSiteManagerName() {
        return siteManagerName;
    }

    public void setSiteManagerPhone(String siteManagerPhone) {
        this.siteManagerPhone = siteManagerPhone;
    }

    public String getSiteManagerPhone() {
        return siteManagerPhone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDailyCapacity(Integer dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }

    public Integer getDailyCapacity() {
        return dailyCapacity;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionName() {
        return regionName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("siteId", getSiteId())
            .append("siteName", getSiteName())
            .append("regionId", getRegionId())
            .append("address", getAddress())
            .append("streetManagerId", getStreetManagerId())
            .append("streetManagerName", getStreetManagerName())
            .append("streetManagerPhone", getStreetManagerPhone())
            .append("siteManagerId", getSiteManagerId())
            .append("siteManagerName", getSiteManagerName())
            .append("siteManagerPhone", getSiteManagerPhone())
            .append("status", getStatus())
            .append("dailyCapacity", getDailyCapacity())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
