package com.javaxiaobear.module.gc.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 居民列表视图对象
 * 
 * @author javaxiaobear
 */
public class ResidentVO {

    /** 居民ID */
    private Long residentId;

    /** 真实姓名 */
    private String realName;

    /** 身份证号（脱敏） */
    private String idCardNo;

    /** 性别 */
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄（计算） */
    private Integer age;

    /** 联系电话 */
    private String phoneNumber;

    /** 省份名称 */
    private String provinceName;

    /** 城市名称 */
    private String cityName;

    /** 区县名称 */
    private String districtName;

    /** 街道/乡镇名称 */
    private String streetName;

    /** 社区/村名称 */
    private String communityName;

    /** 完整区域名称 */
    private String fullRegionName;

    /** 详细地址 */
    private String detailAddress;

    /** 预约采血点名称 */
    private String appointmentSiteName;

    /** 是否重点人群 */
    private Integer isFocusGroup;

    /** 协助录入调查员姓名 */
    private String surveyorName;

    /** 录入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // Getters and Setters
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getFullRegionName() {
        return fullRegionName;
    }

    public void setFullRegionName(String fullRegionName) {
        this.fullRegionName = fullRegionName;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getAppointmentSiteName() {
        return appointmentSiteName;
    }

    public void setAppointmentSiteName(String appointmentSiteName) {
        this.appointmentSiteName = appointmentSiteName;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public String getSurveyorName() {
        return surveyorName;
    }

    public void setSurveyorName(String surveyorName) {
        this.surveyorName = surveyorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
