package com.javaxiaobear.module.gc.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcRegion;
import com.javaxiaobear.module.gc.domain.GcSurveyor;
import com.javaxiaobear.module.gc.domain.vo.SurveyorPerformanceVO;
import com.javaxiaobear.module.gc.mapper.GcRegionMapper;
import com.javaxiaobear.module.gc.mapper.GcSurveyorMapper;
import com.javaxiaobear.module.gc.service.IGcSurveyorService;

/**
 * 问卷调查员Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@Service
public class GcSurveyorServiceImpl implements IGcSurveyorService
{
    @Autowired
    private GcSurveyorMapper surveyorMapper;

    @Autowired
    private GcRegionMapper regionMapper;

    /**
     * 查询问卷调查员列表
     * 
     * @param surveyor 问卷调查员
     * @return 问卷调查员集合
     */
    @Override
    public List<GcSurveyor> selectSurveyorList(GcSurveyor surveyor)
    {
        return surveyorMapper.selectSurveyorList(surveyor);
    }

    /**
     * 根据调查员ID查询信息
     * 
     * @param surveyorId 调查员ID
     * @return 问卷调查员
     */
    @Override
    public GcSurveyor selectSurveyorById(Long surveyorId)
    {
        return surveyorMapper.selectSurveyorById(surveyorId);
    }

    /**
     * 检查userId是否唯一
     * 
     * @param surveyor 调查员信息
     * @return 结果
     */
    @Override
    public boolean checkUserIdUnique(GcSurveyor surveyor)
    {
        Long surveyorId = StringUtils.isNull(surveyor.getSurveyorId()) ? -1L : surveyor.getSurveyorId();
        GcSurveyor info = surveyorMapper.checkUserIdUnique(surveyor.getUserId(), surveyorId);
        if (StringUtils.isNotNull(info) && info.getSurveyorId().longValue() != surveyorId.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 检查调查员是否有关联居民
     * 
     * @param surveyorId 调查员ID
     * @return 结果
     */
    @Override
    public boolean checkSurveyorExistResident(Long surveyorId)
    {
        int result = surveyorMapper.checkSurveyorExistResident(surveyorId);
        return result > 0;
    }

    /**
     * 查询调查员绩效统计
     * 
     * @param regionId 所属街道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 绩效统计列表
     */
    @Override
    public List<SurveyorPerformanceVO> selectSurveyorPerformance(Long regionId, Date startDate, Date endDate)
    {
        return surveyorMapper.selectSurveyorPerformance(regionId, startDate, endDate);
    }

    /**
     * 新增问卷调查员
     * 
     * @param surveyor 问卷调查员
     * @return 结果
     */
    @Override
    public int insertSurveyor(GcSurveyor surveyor)
    {
        // 验证所属区域是否存在且为街道级别
        if (surveyor.getRegionId() != null)
        {
            GcRegion region = regionMapper.selectRegionById(surveyor.getRegionId());
            if (region == null)
            {
                throw new RuntimeException("所属区域不存在");
            }
            if (region.getRegionLevel() != 4)
            {
                throw new RuntimeException("调查员必须归属于街道/乡镇级别区域");
            }
        }

        // 验证手机号格式
        if (StringUtils.isNotEmpty(surveyor.getPhoneNumber()))
        {
            if (!surveyor.getPhoneNumber().matches("^1[3-9]\\d{9}$"))
            {
                throw new RuntimeException("手机号格式不正确");
            }
        }

        // 默认状态为正常
        if (StringUtils.isEmpty(surveyor.getStatus()))
        {
            surveyor.setStatus("0");
        }

        return surveyorMapper.insertSurveyor(surveyor);
    }

    /**
     * 修改问卷调查员
     * 
     * @param surveyor 问卷调查员
     * @return 结果
     */
    @Override
    public int updateSurveyor(GcSurveyor surveyor)
    {
        // 验证手机号格式
        if (StringUtils.isNotEmpty(surveyor.getPhoneNumber()))
        {
            if (!surveyor.getPhoneNumber().matches("^1[3-9]\\d{9}$"))
            {
                throw new RuntimeException("手机号格式不正确");
            }
        }

        return surveyorMapper.updateSurveyor(surveyor);
    }

    /**
     * 删除问卷调查员
     * 
     * @param surveyorId 调查员ID
     * @return 结果
     */
    @Override
    public int deleteSurveyorById(Long surveyorId)
    {
        return surveyorMapper.deleteSurveyorById(surveyorId);
    }
}
