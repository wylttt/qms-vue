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
 * 筛查结果实体类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gc_screening_result")
public class GcScreeningResult extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * 结果ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long resultId;

    /**
     * 居民ID
     */
    private Long residentId;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 筛查类型(1:血液筛查/2:胃镜筛查)
     */
    private String screeningType;

    /**
     * 血液筛查结果(0:未检测/1:中低风险/2:高风险)
     */
    private String bloodResult;

    /**
     * 血液筛查详细说明
     */
    private String bloodResultDetail;

    /**
     * 血液检测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bloodTestDate;

    /**
     * 血液检测机构
     */
    private String bloodTestInstitution;

    /**
     * 胃镜筛查结果(预留字段)
     */
    private String gastroscopyResult;

    /**
     * 胃镜筛查详细说明
     */
    private String gastroscopyResultDetail;

    /**
     * 胃镜检测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gastroscopyTestDate;

    /**
     * 胃镜检测机构
     */
    private String gastroscopyTestInstitution;

    /**
     * 最终风险等级(1:低风险/2:中风险/3:高风险)
     */
    private String riskLevel;

    /**
     * 是否需要随访(0:否/1:是)
     */
    private Integer needFollowUp;

    /**
     * 医生建议
     */
    private String doctorAdvice;

    /**
     * 录入人ID
     */
    private Long inputUserId;

    /**
     * 录入人姓名
     */
    private String inputUserName;

    /**
     * 审核人ID
     */
    private Long reviewUserId;

    /**
     * 审核人姓名
     */
    private String reviewUserName;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /**
     * 审核状态(0:待审核/1:已审核)
     */
    private String reviewStatus;
}
