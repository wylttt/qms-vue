package com.javaxiaobear.module.gc.domain.vo;

import java.math.BigDecimal;

/**
 * 子区域进度视图对象
 * 
 * @author javaxiaobear
 */
public class SubRegionProgressVO {

    /** 区域ID */
    private Long regionId;

    /** 区域名称 */
    private String regionName;

    /** 该区域目标人数 */
    private Integer targetCount;

    /** 该区域已完成人数 */
    private Integer completedCount;

    /** 完成率（%） */
    private BigDecimal completionRate;

    // Getters and Setters
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
}
