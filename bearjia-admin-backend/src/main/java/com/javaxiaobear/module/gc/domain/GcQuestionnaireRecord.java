package com.javaxiaobear.module.gc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaxiaobear.common.core.domain.BaseEntity;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 问卷记录对象 gc_questionnaire_record
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Alias("GcQuestionnaireRecord")
public class GcQuestionnaireRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 问卷记录ID */
    private Long recordId;

    /** 居民ID */
    private Long residentId;

    /** 模板ID */
    private Long templateId;

    /** 调查员ID(协助填写时) */
    private Long surveyorId;

    /** 问卷答案(JSON格式) */
    private String answers;

    /** 风险评分 */
    private Integer riskScore;

    /** 是否重点人群(0否/1是) */
    private Integer isFocusGroup;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 创建来源(resident居民/surveyor调查员) */
    private String createSource;

    // Getter and Setter methods

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

    public Long getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorId(Long surveyorId) {
        this.surveyorId = surveyorId;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Integer getIsFocusGroup() {
        return isFocusGroup;
    }

    public void setIsFocusGroup(Integer isFocusGroup) {
        this.isFocusGroup = isFocusGroup;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getCreateSource() {
        return createSource;
    }

    public void setCreateSource(String createSource) {
        this.createSource = createSource;
    }

    @Override
    public String toString() {
        return "GcQuestionnaireRecord{" +
                "recordId=" + recordId +
                ", residentId=" + residentId +
                ", templateId=" + templateId +
                ", surveyorId=" + surveyorId +
                ", answers='" + answers + '\'' +
                ", riskScore=" + riskScore +
                ", isFocusGroup=" + isFocusGroup +
                ", submitTime=" + submitTime +
                ", createSource='" + createSource + '\'' +
                '}';
    }
}
