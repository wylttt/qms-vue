package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 任务实体类
 * 
 * @author javaxiaobear
 */
public class GcTask extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 任务ID（主键） */
    private Long taskId;

    /** 任务名称 */
    private String taskName;

    /** 任务编号（唯一） */
    private String taskCode;

    /** 任务类型（1问卷/2采血） */
    private String taskType;

    /** 任务状态（0草稿/1进行中/2已结束） */
    private String taskStatus;

    /** 任务描述 */
    private String description;

    /** 目标区域ID */
    private Long targetRegionId;

    /** 目标人数 */
    private Integer targetCount;

    /** 已完成人数 */
    private Integer completedCount;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTargetRegionId() {
        return targetRegionId;
    }

    public void setTargetRegionId(Long targetRegionId) {
        this.targetRegionId = targetRegionId;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "GcTask{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskCode='" + taskCode + '\'' +
                ", taskType='" + taskType + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", description='" + description + '\'' +
                ", targetRegionId=" + targetRegionId +
                ", targetCount=" + targetCount +
                ", completedCount=" + completedCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
