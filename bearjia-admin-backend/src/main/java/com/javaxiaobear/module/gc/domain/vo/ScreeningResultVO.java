package com.javaxiaobear.module.gc.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 筛查结果视图对象
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
public class ScreeningResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    private Long resultId;

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
     * 完整区域名称
     */
    private String fullRegionName;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 采血点名称
     */
    private String siteName;

    /**
     * 筛查类型
     */
    private String screeningType;

    /**
     * 筛查类型文字
     */
    private String screeningTypeText;

    /**
     * 血液筛查结果
     */
    private String bloodResult;

    /**
     * 血液筛查结果文字
     */
    private String bloodResultText;

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
     * 胃镜筛查结果
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
     * 最终风险等级
     */
    private String riskLevel;

    /**
     * 风险等级文字
     */
    private String riskLevelText;

    /**
     * 是否需要随访
     */
    private Integer needFollowUp;

    /**
     * 医生建议
     */
    private String doctorAdvice;

    /**
     * 录入人姓名
     */
    private String inputUserName;

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
     * 审核状态
     */
    private String reviewStatus;

    /**
     * 审核状态文字
     */
    private String reviewStatusText;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 备注
     */
    private String remark;
}
