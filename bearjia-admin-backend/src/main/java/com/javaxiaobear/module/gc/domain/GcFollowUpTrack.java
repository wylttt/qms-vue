package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 随访跟踪记录对象 gc_follow_up_track
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GcFollowUpTrack", description = "随访跟踪记录实体")
public class GcFollowUpTrack extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 跟踪记录ID */
    @ApiModelProperty(value = "跟踪记录ID")
    private Long trackId;

    /** 随访对象ID */
    @ApiModelProperty(value = "随访对象ID", required = true)
    private Long followUpId;

    /** 跟踪日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "跟踪日期", required = true)
    private Date trackDate;

    /** 跟踪方式(phone电话/sms短信/visit上门/other其他) */
    @ApiModelProperty(value = "跟踪方式", notes = "phone电话/sms短信/visit上门/other其他")
    private String trackType;

    /** 跟踪结果(success成功联系/failed未联系上/refused拒绝) */
    @ApiModelProperty(value = "跟踪结果", notes = "success成功联系/failed未联系上/refused拒绝")
    private String trackResult;

    /** 跟踪内容 */
    @ApiModelProperty(value = "跟踪内容", required = true)
    private String trackContent;

    /** 下次跟踪日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "下次跟踪日期")
    private Date nextTrackDate;

    /** 跟踪人员ID */
    @ApiModelProperty(value = "跟踪人员ID", required = true)
    private Long trackerUserId;

    /** 跟踪人员名称 */
    @ApiModelProperty(value = "跟踪人员名称")
    private String trackerUserName;

    // ===== 关联对象 =====
    
    /** 随访对象信息 */
    @ApiModelProperty(value = "随访对象信息")
    private GcFollowUp followUp;
    
    /** 居民信息 */
    @ApiModelProperty(value = "居民信息")
    private GcResident resident;
}
