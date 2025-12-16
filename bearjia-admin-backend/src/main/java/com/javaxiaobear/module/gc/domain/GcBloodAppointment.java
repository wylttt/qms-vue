package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 采血预约对象 gc_blood_appointment
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GcBloodAppointment", description = "采血预约实体")
public class GcBloodAppointment extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 预约ID */
    @ApiModelProperty(value = "预约ID")
    private Long appointmentId;

    /** 居民ID */
    @ApiModelProperty(value = "居民ID", required = true)
    private Long residentId;

    /** 采血点ID */
    @ApiModelProperty(value = "采血点ID", required = true)
    private Long siteId;

    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预约时间", required = true)
    private Date appointmentTime;

    /** 预约状态(pending已预约/cancelled已取消/completed已完成) */
    @ApiModelProperty(value = "预约状态", notes = "pending已预约/cancelled已取消/completed已完成")
    private String appointmentStatus;

    /** 第三方系统预约ID */
    @ApiModelProperty(value = "第三方系统预约ID")
    private String thirdSystemId;

    /** 条码编号(第三方系统生成) */
    @ApiModelProperty(value = "条码编号")
    private String barcodeNumber;

    /** 推送状态(pending待推送/pushing推送中/pushed已推送/failed推送失败) */
    @ApiModelProperty(value = "推送状态", notes = "pending待推送/pushing推送中/pushed已推送/failed推送失败")
    private String pushStatus;

    /** 推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "推送时间")
    private Date pushTime;

    /** 重试次数 */
    @ApiModelProperty(value = "重试次数")
    private Integer retryTimes;

    /** 采样状态(0未采样/1已采样) */
    @ApiModelProperty(value = "采样状态", notes = "0未采样/1已采样")
    private String samplingStatus;

    /** 采样时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "采样时间")
    private Date samplingTime;

    // ===== 关联对象字段 =====
    
    /** 居民姓名 */
    @ApiModelProperty(value = "居民姓名")
    private String residentName;
    
    /** 居民手机号 */
    @ApiModelProperty(value = "居民手机号")
    private String contactPhone;
    
    /** 采血点名称 */
    @ApiModelProperty(value = "采血点名称")
    private String siteName;
    
    /** 采血点地址 */
    @ApiModelProperty(value = "采血点地址")
    private String siteAddress;
}
