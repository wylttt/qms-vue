package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 随访对象对象 gc_follow_up
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GcFollowUp", description = "随访对象实体")
public class GcFollowUp extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 随访对象ID */
    @ApiModelProperty(value = "随访对象ID")
    private Long followUpId;

    /** 居民ID */
    @ApiModelProperty(value = "居民ID", required = true)
    private Long residentId;

    /** 筛查结果ID */
    @ApiModelProperty(value = "筛查结果ID", required = true)
    private Long resultId;

    /** 随访类型(blood_high_risk血液筛查高风险/gastroscopy_abnormal胃镜异常等) */
    @ApiModelProperty(value = "随访类型", notes = "blood_high_risk血液筛查高风险/gastroscopy_abnormal胃镜异常")
    private String followUpType;

    /** 随访状态(pending待随访/assigned已分配/in_progress随访中/completed已完成) */
    @ApiModelProperty(value = "随访状态", notes = "pending待随访/assigned已分配/in_progress随访中/completed已完成")
    private String followUpStatus;

    /** 优先级(high高/medium中/low低) */
    @ApiModelProperty(value = "优先级", notes = "high高/medium中/low低")
    private String followUpLevel;

    /** 分配给的用户ID */
    @ApiModelProperty(value = "分配给的用户ID")
    private Long assignedUserId;

    /** 分配给的用户名称 */
    @ApiModelProperty(value = "分配给的用户名称")
    private String assignedUserName;

    /** 分配日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "分配日期")
    private Date assignDate;

    /** 完成日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "完成日期")
    private Date completeDate;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    // ===== 关联对象 =====
    
    /** 居民信息 */
    @ApiModelProperty(value = "居民信息")
    private GcResident resident;
    
    /** 筛查结果信息 */
    @ApiModelProperty(value = "筛查结果信息")
    private GcScreeningResult screeningResult;
}
