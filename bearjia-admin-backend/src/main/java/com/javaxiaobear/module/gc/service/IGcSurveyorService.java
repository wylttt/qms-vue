package com.javaxiaobear.module.gc.service;

import java.util.Date;
import java.util.List;
import com.javaxiaobear.module.gc.domain.GcSurveyor;
import com.javaxiaobear.module.gc.domain.vo.SurveyorPerformanceVO;

/**
 * 问卷调查员Service接口
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
public interface IGcSurveyorService
{
    /**
     * 查询问卷调查员列表
     * 
     * @param surveyor 问卷调查员
     * @return 问卷调查员集合
     */
    public List<GcSurveyor> selectSurveyorList(GcSurveyor surveyor);

    /**
     * 根据调查员ID查询信息
     * 
     * @param surveyorId 调查员ID
     * @return 问卷调查员
     */
    public GcSurveyor selectSurveyorById(Long surveyorId);

    /**
     * 检查userId是否唯一
     * 
     * @param surveyor 调查员信息
     * @return 结果
     */
    public boolean checkUserIdUnique(GcSurveyor surveyor);

    /**
     * 检查调查员是否有关联居民
     * 
     * @param surveyorId 调查员ID
     * @return 结果
     */
    public boolean checkSurveyorExistResident(Long surveyorId);

    /**
     * 查询调查员绩效统计
     * 
     * @param regionId 所属街道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 绩效统计列表
     */
    public List<SurveyorPerformanceVO> selectSurveyorPerformance(Long regionId, Date startDate, Date endDate);

    /**
     * 新增问卷调查员
     * 
     * @param surveyor 问卷调查员
     * @return 结果
     */
    public int insertSurveyor(GcSurveyor surveyor);

    /**
     * 修改问卷调查员
     * 
     * @param surveyor 问卷调查员
     * @return 结果
     */
    public int updateSurveyor(GcSurveyor surveyor);

    /**
     * 删除问卷调查员
     * 
     * @param surveyorId 调查员ID
     * @return 结果
     */
    public int deleteSurveyorById(Long surveyorId);
}
