package com.javaxiaobear.module.gc.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 居民查询条件对象
 * 
 * @author javaxiaobear
 */
public class ResidentQueryDTO {

    /** 姓名（模糊查询） */
    private String realName;

    /** 身份证号 */
    private String idCardNo;

    /** 联系电话 */
    private String phoneNumber;

    /** 区域ID（任意层级） */
    private Long regionId;

    /** 采血点ID */
    private Long appointmentSiteId;

    /** 是否重点人群 */
    private Integer isFocusGroup;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 页码 */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    // Getters and Setters
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getAppointmentSiteId() {
        return appointmentSiteId;
    }

    public void setAppointmentSiteId(Long appointmentSiteId) {
        this.appointmentSiteId = appointmentSiteId;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
