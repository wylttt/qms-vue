package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 筛查结果对象 gc_screening_result
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GcScreeningResult", description = "筛查结果实体")
public class GcScreeningResult extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 结果ID */
    @ApiModelProperty(value = "结果ID")
    private Long resultId;

    /** 居民ID */
    @ApiModelProperty(value = "居民ID", required = true)
    private Long residentId;

    /** 筛查点ID */
    @ApiModelProperty(value = "筛查点ID", required = true)
    private Long siteId;

    /** 血液筛查结果(not_tested未检测/low_risk中低风险/high_risk高风险) */
    @ApiModelProperty(value = "血液筛查结果", notes = "not_tested未检测/low_risk中低风险/high_risk高风险")
    private String bloodResult;

    /** 血液筛查详细结果 */
    @ApiModelProperty(value = "血液筛查详细结果")
    private String bloodResultDetail;

    /** 血液筛查日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "血液筛查日期")
    private Date bloodResultDate;

    /** 胃镜筛查结果(预留) */
    @ApiModelProperty(value = "胃镜筛查结果")
    private String gastroscopyResult;

    /** 胃镜筛查详细结果(预留) */
    @ApiModelProperty(value = "胃镜筛查详细结果")
    private String gastroscopyResultDetail;

    /** 胃镜检查日期(预留) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "胃镜检查日期")
    private Date gastroscopyDate;

    // ===== 关联对象字段 =====
    
    /** 居民姓名 */
    @ApiModelProperty(value = "居民姓名")
    private String residentName;
    
    /** 性别 */
    @ApiModelProperty(value = "性别")
    private String gender;
    
    /** 年龄 */
    @ApiModelProperty(value = "年龄")
    private Integer age;
    
    /** 联系电话 */
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;
    
    /** 筛查点名称 */
    @ApiModelProperty(value = "筛查点名称")
    private String siteName;
    
    /** 区县 */
    @ApiModelProperty(value = "区县")
    private String district;
    
    /** 街道/乡镇 */
    @ApiModelProperty(value = "街道/乡镇")
    private String street;
}
