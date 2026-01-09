package com.javaxiaobear.module.gc.domain.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 任务进度视图对象
 * 
 * @author javaxiaobear
 */
public class TaskProgressVO {

    /** 任务ID */
    private Long taskId;

    /** 任务名称 */
    private String taskName;

    /** 任务类型 */
    private String taskType;

    /** 目标区域名称 */
    private String targetRegionName;

    /** 目标人数 */
    private Integer targetCount;

    /** 已完成人数 */
    private Integer completedCount;

    /** 完成率（%） */
    private BigDecimal completionRate;

    /** 下级区域进度列表 */
    private List<SubRegionProgressVO> subRegionProgress;

    // Getters and Setters
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

    public String getTargetRegionName() {
        return targetRegionName;
    }

    public void setTargetRegionName(String targetRegionName) {
        this.targetRegionName = targetRegionName;
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

    public List<SubRegionProgressVO> getSubRegionProgress() {
        return subRegionProgress;
    }

    public void setSubRegionProgress(List<SubRegionProgressVO> subRegionProgress) {
        this.subRegionProgress = subRegionProgress;
    }
}
