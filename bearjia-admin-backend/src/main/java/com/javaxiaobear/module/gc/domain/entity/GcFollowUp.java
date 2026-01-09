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
 * 随访对象实体类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gc_follow_up")
public class GcFollowUp extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * 随访ID(主键)
     */
    @TableId(type = IdType.AUTO)
    private Long followUpId;

    /**
     * 居民ID
     */
    private Long residentId;

    /**
     * 筛查结果ID
     */
    private Long resultId;

    /**
     * 随访原因(1:高风险人群/2:异常结果/3:医生建议)
     */
    private String followUpReason;

    /**
     * 随访状态(0:待随访/1:随访中/2:已完成)
     */
    private String followUpStatus;

    /**
     * 随访跟踪员ID
     */
    private Long trackerUserId;

    /**
     * 随访跟踪员姓名
     */
    private String trackerUserName;

    /**
     * 计划随访次数
     */
    private Integer plannedVisitCount;

    /**
     * 实际随访次数
     */
    private Integer actualVisitCount;

    /**
     * 开始随访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 结束随访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 最后随访时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastVisitTime;

    /**
     * 随访结论
     */
    private String followUpConclusion;
}
