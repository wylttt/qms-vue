package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 问卷记录实体类
 * 
 * @author javaxiaobear
 */
public class GcQuestionnaireRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 问卷记录ID（主键） */
    private Long recordId;

    /** 居民ID */
    private Long residentId;

    /** 问卷模板ID */
    private Long templateId;

    /** 答案内容（JSON字符串） */
    private String answerContent;

    /** 总分 */
    private Integer totalScore;

    /** 是否重点人群（0否/1是） */
    private Integer isFocusGroup;

    /** 协助填写调查员ID */
    private Long surveyorId;

    /** 填写时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fillTime;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public Long getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorId(Long surveyorId) {
        this.surveyorId = surveyorId;
    }

    public Date getFillTime() {
        return fillTime;
    }

    public void setFillTime(Date fillTime) {
        this.fillTime = fillTime;
    }

    @Override
    public String toString() {
        return "GcQuestionnaireRecord{" +
                "recordId=" + recordId +
                ", residentId=" + residentId +
                ", templateId=" + templateId +
                ", totalScore=" + totalScore +
                ", isFocusGroup=" + isFocusGroup +
                ", surveyorId=" + surveyorId +
                ", fillTime=" + fillTime +
                '}';
    }
}
