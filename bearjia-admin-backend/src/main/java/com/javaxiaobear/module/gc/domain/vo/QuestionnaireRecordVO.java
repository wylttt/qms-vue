package com.javaxiaobear.module.gc.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 问卷记录视图对象
 * 
 * @author javaxiaobear
 */
public class QuestionnaireRecordVO {

    /** 问卷记录ID */
    private Long recordId;

    /** 居民姓名 */
    private String residentName;

    /** 身份证号（脱敏） */
    private String idCardNo;

    /** 所属区域（完整） */
    private String fullRegionName;

    /** 问卷模板名称 */
    private String templateName;

    /** 总分 */
    private Integer totalScore;

    /** 是否重点人群 */
    private Integer isFocusGroup;

    /** 协助填写调查员姓名 */
    private String surveyorName;

    /** 填写时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fillTime;

    // Getters and Setters
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getFullRegionName() {
        return fullRegionName;
    }

    public void setFullRegionName(String fullRegionName) {
        this.fullRegionName = fullRegionName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public String getSurveyorName() {
        return surveyorName;
    }

    public void setSurveyorName(String surveyorName) {
        this.surveyorName = surveyorName;
    }

    public Date getFillTime() {
        return fillTime;
    }

    public void setFillTime(Date fillTime) {
        this.fillTime = fillTime;
    }
}
