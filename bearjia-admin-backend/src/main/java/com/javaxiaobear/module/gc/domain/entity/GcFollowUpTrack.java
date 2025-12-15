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
 * 随访跟踪记录实体类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gc_follow_up_track")
public class GcFollowUpTrack extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * 跟踪记录ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long trackId;

    /**
     * 随访ID
     */
    private Long followUpId;

    /**
     * 随访时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime;

    /**
     * 随访方式(1:电话/2:上门/3:微信)
     */
    private String visitMethod;

    /**
     * 随访内容
     */
    private String visitContent;

    /**
     * 居民反馈
     */
    private String residentFeedback;

    /**
     * 健康状况
     */
    private String healthStatus;

    /**
     * 是否遵医嘱(0:否/1:是)
     */
    private Integer followDoctorAdvice;

    /**
     * 下次随访计划日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextVisitDate;

    /**
     * 随访人ID
     */
    private Long visitorUserId;

    /**
     * 随访人姓名
     */
    private String visitorUserName;

    /**
     * 附件URL(多个用逗号分隔)
     */
    private String attachmentUrls;
}
