package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 居民信息实体类
 * 
 * @author javaxiaobear
 */
public class GcResident extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 居民ID（主键） */
    private Long residentId;

    /** 真实姓名 */
    private String realName;

    /** 身份证号（唯一） */
    private String idCardNo;

    /** 性别（0男/1女） */
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /** 联系电话 */
    private String phoneNumber;

    /** 省份ID */
    private Long provinceId;

    /** 城市ID */
    private Long cityId;

    /** 区县ID */
    private Long districtId;

    /** 街道/乡镇ID */
    private Long streetId;

    /** 社区/村ID */
    private Long communityId;

    /** 详细地址 */
    private String detailAddress;

    /** 预约采血点ID */
    private Long appointmentSiteId;

    /** 协助录入调查员ID */
    private Long surveyorId;

    /** 是否重点人群（0否/1是） */
    private Integer isFocusGroup;

    /** 录入方式（0居民自主/1调查员协助） */
    private String registrationType;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Long getAppointmentSiteId() {
        return appointmentSiteId;
    }

    public void setAppointmentSiteId(Long appointmentSiteId) {
        this.appointmentSiteId = appointmentSiteId;
    }

    public Long getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorId(Long surveyorId) {
        this.surveyorId = surveyorId;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    @Override
    public String toString() {
        return "GcResident{" +
                "residentId=" + residentId +
                ", realName='" + realName + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", provinceId=" + provinceId +
                ", cityId=" + cityId +
                ", districtId=" + districtId +
                ", streetId=" + streetId +
                ", communityId=" + communityId +
                ", detailAddress='" + detailAddress + '\'' +
                ", appointmentSiteId=" + appointmentSiteId +
                ", surveyorId=" + surveyorId +
                ", isFocusGroup=" + isFocusGroup +
                ", registrationType='" + registrationType + '\'' +
                '}';
    }
}
