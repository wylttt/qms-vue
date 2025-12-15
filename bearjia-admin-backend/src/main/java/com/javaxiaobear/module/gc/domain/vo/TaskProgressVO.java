package com.javaxiaobear.module.gc.domain.vo;

import java.math.BigDecimal;

/**
 * 任务进度统计视图对象
 * 用于展示任务完成进度和统计信息
 * 
 * @author Bear
 * @date 2025-01-09
 */
public class TaskProgressVO {
    
    /** 任务ID */
    private Long taskId;

    /** 任务名称 */
    private String taskName;

    /** 任务类型 */
    private String taskType;

    /** 区域ID */
    private Long regionId;

    /** 区域名称 */
    private String regionName;

    /** 目标数量 */
    private Integer targetCount;

    /** 已完成数量 */
    private Integer completedCount;

    /** 完成率(%) */
    private BigDecimal completionRate;

    /** 任务状态 */
    private String status;

    // Getter and Setter methods

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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
