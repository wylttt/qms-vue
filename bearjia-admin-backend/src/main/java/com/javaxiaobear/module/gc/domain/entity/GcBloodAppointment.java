package com.javaxiaobear.module.gc.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 采血预约实体类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gc_blood_appointment")
public class GcBloodAppointment extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long appointmentId;

    /**
     * 居民ID
     */
    private Long residentId;

    /**
     * 采血点ID
     */
    private Long appointmentSiteId;

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
     * 预约状态(0:待确认/1:已确认/2:已完成/3:已取消)
     */
    private String appointmentStatus;

    /**
     * 采样状态(0:未采样/1:已采样)
     */
    private String samplingStatus;

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
     * 采血样本编号(采样后生成)
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
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;
}
