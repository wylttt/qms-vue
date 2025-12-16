package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 推送日志对象 gc_push_log
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GcPushLog", description = "推送日志实体")
public class GcPushLog extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @ApiModelProperty(value = "日志ID")
    private Long logId;

    /** 预约ID */
    @ApiModelProperty(value = "预约ID", required = true)
    private Long appointmentId;

    /** 请求URL */
    @ApiModelProperty(value = "请求URL", required = true)
    private String requestUrl;

    /** 请求体(JSON) */
    @ApiModelProperty(value = "请求体")
    private String requestBody;

    /** 响应码 */
    @ApiModelProperty(value = "响应码")
    private Integer responseCode;

    /** 响应体(JSON) */
    @ApiModelProperty(value = "响应体")
    private String responseBody;

    /** 推送状态(success成功/failed失败) */
    @ApiModelProperty(value = "推送状态", notes = "success成功/failed失败")
    private String pushStatus;

    /** 重试次数 */
    @ApiModelProperty(value = "重试次数")
    private Integer retryTimes;

    /** 推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "推送时间")
    private Date pushTime;

    /** 耗时(毫秒) */
    @ApiModelProperty(value = "耗时(毫秒)")
    private Integer costTime;

    /** 错误信息 */
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

    // ===== 关联对象字段 =====
    
    /** 居民姓名 */
    @ApiModelProperty(value = "居民姓名")
    private String residentName;
    
    /** 采血点名称 */
    @ApiModelProperty(value = "采血点名称")
    private String siteName;
}
