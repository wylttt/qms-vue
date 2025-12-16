package com.javaxiaobear.module.gc.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 筛查结果统计视图对象
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@ApiModel(value = "ScreeningStatisticsVO", description = "筛查结果统计视图")
public class ScreeningStatisticsVO {
    
    /** 筛查点ID */
    @ApiModelProperty(value = "筛查点ID")
    private Long siteId;
    
    /** 筛查点名称 */
    @ApiModelProperty(value = "筛查点名称")
    private String siteName;
    
    /** 问卷重点人数 */
    @ApiModelProperty(value = "问卷重点人数")
    private Integer focusGroupCount;
    
    /** 问卷非重点人数 */
    @ApiModelProperty(value = "问卷非重点人数")
    private Integer nonFocusGroupCount;
    
    /** 血液筛查参与数 */
    @ApiModelProperty(value = "血液筛查参与数")
    private Integer bloodParticipantCount;
    
    /** 中低风险人数 */
    @ApiModelProperty(value = "中低风险人数")
    private Integer lowRiskCount;
    
    /** 高风险人数 */
    @ApiModelProperty(value = "高风险人数")
    private Integer highRiskCount;
    
    /** 进行胃镜人数 */
    @ApiModelProperty(value = "进行胃镜人数")
    private Integer gastroscopyCount;
}
