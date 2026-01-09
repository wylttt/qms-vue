package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.entity.GcSurveyor;
import java.util.List;

/**
 * 问卷调查员Service接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface IGcSurveyorService {
    /**
     * 查询问卷调查员
     * 
     * @param surveyorId 问卷调查员主键
     * @return 问卷调查员
     */
    public GcSurveyor selectGcSurveyorBySurveyorId(Long surveyorId);

    /**
     * 查询问卷调查员列表
     * 
     * @param gcSurveyor 问卷调查员
     * @return 问卷调查员集合
     */
    public List<GcSurveyor> selectGcSurveyorList(GcSurveyor gcSurveyor);

    /**
     * 新增问卷调查员
     * 
     * @param gcSurveyor 问卷调查员
     * @return 结果
     */
    public int insertGcSurveyor(GcSurveyor gcSurveyor);

    /**
     * 修改问卷调查员
     * 
     * @param gcSurveyor 问卷调查员
     * @return 结果
     */
    public int updateGcSurveyor(GcSurveyor gcSurveyor);

    /**
     * 批量删除问卷调查员
     * 
     * @param surveyorIds 需要删除的问卷调查员主键集合
     * @return 结果
     */
    public int deleteGcSurveyorBySurveyorIds(Long[] surveyorIds);

    /**
     * 删除问卷调查员信息
     * 
     * @param surveyorId 问卷调查员主键
     * @return 结果
     */
    public int deleteGcSurveyorBySurveyorId(Long surveyorId);

    /**
     * 检查身份证号是否唯一
     * 
     * @param idCard 身份证号
     * @param surveyorId 调查员ID(更新时排除自己)
     * @return true唯一 false不唯一
     */
    public boolean checkIdCardUnique(String idCard, Long surveyorId);
}
