package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.entity.GcSurveyor;
import java.util.List;

/**
 * 问卷调查员Mapper接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface GcSurveyorMapper {
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
     * 删除问卷调查员
     * 
     * @param surveyorId 问卷调查员主键
     * @return 结果
     */
    public int deleteGcSurveyorBySurveyorId(Long surveyorId);

    /**
     * 批量删除问卷调查员
     * 
     * @param surveyorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGcSurveyorBySurveyorIds(Long[] surveyorIds);

    /**
     * 检查身份证号是否存在
     * 
     * @param idCard 身份证号
     * @param surveyorId 调查员ID(更新时排除自己)
     * @return 结果
     */
    public GcSurveyor checkIdCardUnique(String idCard, Long surveyorId);
}
