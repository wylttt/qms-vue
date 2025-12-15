package com.javaxiaobear.module.gc.domain.vo;

/**
 * 调查员绩效视图对象
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public class SurveyorPerformanceVO
{
    /** 调查员ID */
    private Long surveyorId;

    /** 调查员姓名 */
    private String surveyorName;

    /** 所属街道名称 */
    private String regionName;

    /** 协助录入居民数 */
    private Integer residentCount;

    /** 问卷完成数 */
    private Integer questionnaireCount;

    /** 重点人群数 */
    private Integer focusGroupCount;

    public Long getSurveyorId()
    {
        return surveyorId;
    }

    public void setSurveyorId(Long surveyorId)
    {
        this.surveyorId = surveyorId;
    }

    public String getSurveyorName()
    {
        return surveyorName;
    }

    public void setSurveyorName(String surveyorName)
    {
        this.surveyorName = surveyorName;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public Integer getResidentCount()
    {
        return residentCount;
    }

    public void setResidentCount(Integer residentCount)
    {
        this.residentCount = residentCount;
    }

    public Integer getQuestionnaireCount()
    {
        return questionnaireCount;
    }

    public void setQuestionnaireCount(Integer questionnaireCount)
    {
        this.questionnaireCount = questionnaireCount;
    }

    public Integer getFocusGroupCount()
    {
        return focusGroupCount;
    }

    public void setFocusGroupCount(Integer focusGroupCount)
    {
        this.focusGroupCount = focusGroupCount;
    }
}
