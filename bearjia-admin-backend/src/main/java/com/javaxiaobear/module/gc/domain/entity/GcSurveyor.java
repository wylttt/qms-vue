package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 问卷调查员对象 gc_surveyor
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public class GcSurveyor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 调查员ID */
    private Long surveyorId;

    /** 调查员姓名 */
    private String surveyorName;

    /** 性别(0男/1女) */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 身份证号 */
    private String idCard;

    /** 所属街道/乡镇ID */
    private Long regionId;

    /** 所属社区/村委会ID */
    private Long communityId;

    /** 所属采血点ID */
    private Long siteId;

    /** 状态(0正常/1停用) */
    private String status;

    /** 区域名称(关联查询字段) */
    private String regionName;

    /** 社区名称(关联查询字段) */
    private String communityName;

    /** 采血点名称(关联查询字段) */
    private String siteName;

    public void setSurveyorId(Long surveyorId) {
        this.surveyorId = surveyorId;
    }

    public Long getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorName(String surveyorName) {
        this.surveyorName = surveyorName;
    }

    public String getSurveyorName() {
        return surveyorName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("surveyorId", getSurveyorId())
            .append("surveyorName", getSurveyorName())
            .append("gender", getGender())
            .append("phone", getPhone())
            .append("idCard", getIdCard())
            .append("regionId", getRegionId())
            .append("communityId", getCommunityId())
            .append("siteId", getSiteId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
