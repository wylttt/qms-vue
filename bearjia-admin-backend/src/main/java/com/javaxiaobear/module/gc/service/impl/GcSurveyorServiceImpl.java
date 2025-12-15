package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.module.gc.domain.entity.GcSurveyor;
import com.javaxiaobear.module.gc.mapper.GcSurveyorMapper;
import com.javaxiaobear.module.gc.service.IGcSurveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问卷调查员Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcSurveyorServiceImpl implements IGcSurveyorService {
    @Autowired
    private GcSurveyorMapper gcSurveyorMapper;

    /**
     * 查询问卷调查员
     * 
     * @param surveyorId 问卷调查员主键
     * @return 问卷调查员
     */
    @Override
    public GcSurveyor selectGcSurveyorBySurveyorId(Long surveyorId) {
        return gcSurveyorMapper.selectGcSurveyorBySurveyorId(surveyorId);
    }

    /**
     * 查询问卷调查员列表
     * 
     * @param gcSurveyor 问卷调查员
     * @return 问卷调查员
     */
    @Override
    public List<GcSurveyor> selectGcSurveyorList(GcSurveyor gcSurveyor) {
        return gcSurveyorMapper.selectGcSurveyorList(gcSurveyor);
    }

    /**
     * 新增问卷调查员
     * 
     * @param gcSurveyor 问卷调查员
     * @return 结果
     */
    @Override
    public int insertGcSurveyor(GcSurveyor gcSurveyor) {
        // 设置默认状态为正常
        if (gcSurveyor.getStatus() == null) {
            gcSurveyor.setStatus("0");
        }
        return gcSurveyorMapper.insertGcSurveyor(gcSurveyor);
    }

    /**
     * 修改问卷调查员
     * 
     * @param gcSurveyor 问卷调查员
     * @return 结果
     */
    @Override
    public int updateGcSurveyor(GcSurveyor gcSurveyor) {
        return gcSurveyorMapper.updateGcSurveyor(gcSurveyor);
    }

    /**
     * 批量删除问卷调查员
     * 
     * @param surveyorIds 需要删除的问卷调查员主键
     * @return 结果
     */
    @Override
    public int deleteGcSurveyorBySurveyorIds(Long[] surveyorIds) {
        return gcSurveyorMapper.deleteGcSurveyorBySurveyorIds(surveyorIds);
    }

    /**
     * 删除问卷调查员信息
     * 
     * @param surveyorId 问卷调查员主键
     * @return 结果
     */
    @Override
    public int deleteGcSurveyorBySurveyorId(Long surveyorId) {
        return gcSurveyorMapper.deleteGcSurveyorBySurveyorId(surveyorId);
    }

    /**
     * 检查身份证号是否唯一
     * 
     * @param idCard 身份证号
     * @param surveyorId 调查员ID(更新时排除自己)
     * @return true唯一 false不唯一
     */
    @Override
    public boolean checkIdCardUnique(String idCard, Long surveyorId) {
        GcSurveyor surveyor = gcSurveyorMapper.checkIdCardUnique(idCard, surveyorId);
        return surveyor == null;
    }
}
