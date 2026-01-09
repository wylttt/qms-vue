package com.javaxiaobear.module.gc.domain.dto;

/**
 * 居民导入数据对象
 * 
 * @author javaxiaobear
 */
public class ResidentImportDTO {

    /** 姓名 */
    private String realName;

    /** 身份证号 */
    private String idCardNo;

    /** 联系电话 */
    private String phoneNumber;

    /** 省份名称 */
    private String provinceName;

    /** 城市名称 */
    private String cityName;

    /** 区县名称 */
    private String districtName;

    /** 街道名称 */
    private String streetName;

    /** 社区名称 */
    private String communityName;

    /** 详细地址 */
    private String detailAddress;

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

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
