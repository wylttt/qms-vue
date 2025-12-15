package com.javaxiaobear.module.gc.domain;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 问卷调查员对象 gc_surveyor
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public class GcSurveyor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 调查员ID */
    private Long surveyorId;

    /** 关联用户ID */
    private Long userId;

    /** 真实姓名 */
    private String realName;

    /** 性别(0男/1女) */
    private String gender;

    /** 所属街道/乡镇ID */
    private Long regionId;

    /** 街道/乡镇管理员ID */
    private Long streetManagerId;

    /** 街道/乡镇管理员联系方式 */
    private String streetManagerPhone;

    /** 调查员联系方式 */
    private String phoneNumber;

    /** 状态(0正常/1停用) */
    private String status;

    /** 所属街道/乡镇名称 */
    private String regionName;

    /** 街道/乡镇管理员姓名 */
    private String streetManagerName;

    public void setSurveyorId(Long surveyorId) 
    {
        this.surveyorId = surveyorId;
    }

    public Long getSurveyorId() 
    {
        return surveyorId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRealName(String realName) 
    {
        this.realName = realName;
    }

    public String getRealName() 
    {
        return realName;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setRegionId(Long regionId) 
    {
        this.regionId = regionId;
    }

    public Long getRegionId() 
    {
        return regionId;
    }

    public void setStreetManagerId(Long streetManagerId) 
    {
        this.streetManagerId = streetManagerId;
    }

    public Long getStreetManagerId() 
    {
        return streetManagerId;
    }

    public void setStreetManagerPhone(String streetManagerPhone) 
    {
        this.streetManagerPhone = streetManagerPhone;
    }

    public String getStreetManagerPhone() 
    {
        return streetManagerPhone;
    }

    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setRegionName(String regionName) 
    {
        this.regionName = regionName;
    }

    public String getRegionName() 
    {
        return regionName;
    }

    public void setStreetManagerName(String streetManagerName) 
    {
        this.streetManagerName = streetManagerName;
    }

    public String getStreetManagerName() 
    {
        return streetManagerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("surveyorId", getSurveyorId())
            .append("userId", getUserId())
            .append("realName", getRealName())
            .append("gender", getGender())
            .append("regionId", getRegionId())
            .append("streetManagerId", getStreetManagerId())
            .append("streetManagerPhone", getStreetManagerPhone())
            .append("phoneNumber", getPhoneNumber())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
