package com.javaxiaobear.module.gc.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 采血预约视图对象
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
public class BloodAppointmentVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 居民ID
     */
    private Long residentId;

    /**
     * 居民姓名
     */
    private String residentName;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 联系电话
     */
    private String phoneNumber;

    /**
     * 完整区域名称(省+市+区+街道+社区)
     */
    private String fullRegionName;

    /**
     * 采血点ID
     */
    private Long appointmentSiteId;

    /**
     * 采血点名称
     */
    private String siteName;

    /**
     * 采血点地址
     */
    private String siteAddress;

    /**
     * 预约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    /**
     * 预约时间段(1:上午/2:下午)
     */
    private String appointmentPeriod;

    /**
     * 预约时间段文字
     */
    private String appointmentPeriodText;

    /**
     * 预约状态(0:待确认/1:已确认/2:已完成/3:已取消)
     */
    private String appointmentStatus;

    /**
     * 预约状态文字
     */
    private String appointmentStatusText;

    /**
     * 采样状态(0:未采样/1:已采样)
     */
    private String samplingStatus;

    /**
     * 采样状态文字
     */
    private String samplingStatusText;

    /**
     * 采样时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date samplingTime;

    /**
     * 采样员姓名
     */
    private String samplerName;

    /**
     * 采血样本编号
     */
    private String sampleCode;

    /**
     * 是否已推送第三方系统(0:否/1:是)
     */
    private Integer isPushed;

    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushTime;

    /**
     * 推送状态(0:未推送/1:推送成功/2:推送失败)
     */
    private String pushStatus;

    /**
     * 推送状态文字
     */
    private String pushStatusText;

    /**
     * 推送失败原因
     */
    private String pushFailReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 备注
     */
    private String remark;
}
