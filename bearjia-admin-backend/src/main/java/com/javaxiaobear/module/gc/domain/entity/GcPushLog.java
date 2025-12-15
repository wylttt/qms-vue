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
 * 推送日志实体类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gc_push_log")
public class GcPushLog extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 业务类型(1:采血预约推送)
     */
    private String businessType;

    /**
     * 业务ID(如:预约ID)
     */
    private Long businessId;

    /**
     * 推送方向(1:推送到第三方/2:接收第三方回调)
     */
    private String pushDirection;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方法(GET/POST/PUT/DELETE)
     */
    private String requestMethod;

    /**
     * 请求头
     */
    private String requestHeaders;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 响应状态码
     */
    private Integer responseStatus;

    /**
     * 响应头
     */
    private String responseHeaders;

    /**
     * 响应体
     */
    private String responseBody;

    /**
     * 请求耗时(毫秒)
     */
    private Long costTime;

    /**
     * 推送状态(0:待推送/1:推送成功/2:推送失败)
     */
    private String pushStatus;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextRetryTime;

    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pushTime;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;
}
