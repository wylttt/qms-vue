package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 居民信息对象 gc_resident
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Alias("GcResident")
public class GcResident extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 居民ID */
    private Long residentId;

    /** 姓名 */
    private String name;

    /** 年龄 */
    private Integer age;

    /** 性别(0男/1女) */
    private String gender;

    /** 身份证号 */
    private String idCardNo;

    /** 详细住址 */
    private String address;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区县 */
    private String district;

    /** 街道/乡镇 */
    private String street;

    /** 社区/村 */
    private String community;

    /** 联系方式 */
    private String contactPhone;

    /** 是否重点人群(0否/1是) */
    private Integer isFocusGroup;

    /** 问卷调查员ID */
    private Long surveyorId;

    /** 预约采样点ID */
    private Long appointmentSiteId;

    /** 预约采样时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date appointmentTime;

    /** 采样状态(0未采样/1已采样) */
    private String samplingStatus;

    /** 创建来源(resident居民/surveyor调查员) */
    private String createSource;

    // Getter and Setter methods

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public Long getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorId(Long surveyorId) {
        this.surveyorId = surveyorId;
    }

    public Long getAppointmentSiteId() {
        return appointmentSiteId;
    }

    public void setAppointmentSiteId(Long appointmentSiteId) {
        this.appointmentSiteId = appointmentSiteId;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getSamplingStatus() {
        return samplingStatus;
    }

    public void setSamplingStatus(String samplingStatus) {
        this.samplingStatus = samplingStatus;
    }

    public String getCreateSource() {
        return createSource;
    }

    public void setCreateSource(String createSource) {
        this.createSource = createSource;
    }

    @Override
    public String toString() {
        return "GcResident{" +
                "residentId=" + residentId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", address='" + address + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", community='" + community + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", isFocusGroup=" + isFocusGroup +
                ", surveyorId=" + surveyorId +
                ", appointmentSiteId=" + appointmentSiteId +
                ", appointmentTime=" + appointmentTime +
                ", samplingStatus='" + samplingStatus + '\'' +
                ", createSource='" + createSource + '\'' +
                '}';
    }
}
