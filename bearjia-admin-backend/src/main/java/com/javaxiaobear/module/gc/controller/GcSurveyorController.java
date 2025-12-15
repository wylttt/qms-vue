package com.javaxiaobear.module.gc.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcSurveyor;
import com.javaxiaobear.module.gc.domain.vo.SurveyorPerformanceVO;
import com.javaxiaobear.module.gc.service.IGcSurveyorService;

/**
 * 问卷调查员Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/gc/surveyor")
public class GcSurveyorController extends BaseController
{
    @Autowired
    private IGcSurveyorService surveyorService;

    /**
     * 查询问卷调查员列表
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcSurveyor surveyor)
    {
        startPage();
        List<GcSurveyor> list = surveyorService.selectSurveyorList(surveyor);
        return getDataTable(list);
    }

    /**
     * 查询调查员绩效统计
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:performance')")
    @GetMapping("/performance")
    public TableDataInfo performance(@RequestParam(value = "regionId", required = false) Long regionId,
                                     @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                     @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate)
    {
        startPage();
        List<SurveyorPerformanceVO> list = surveyorService.selectSurveyorPerformance(regionId, startDate, endDate);
        return getDataTable(list);
    }

    /**
     * 获取问卷调查员详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:query')")
    @GetMapping(value = "/{surveyorId}")
    public AjaxResult getInfo(@PathVariable("surveyorId") Long surveyorId)
    {
        return success(surveyorService.selectSurveyorById(surveyorId));
    }

    /**
     * 新增问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:add')")
    @Log(title = "问卷调查员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcSurveyor surveyor)
    {
        if (!surveyorService.checkUserIdUnique(surveyor))
        {
            return error("新增调查员失败,该用户已关联其他调查员");
        }

        surveyor.setCreateBy(getUsername());
        return toAjax(surveyorService.insertSurveyor(surveyor));
    }

    /**
     * 修改问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:edit')")
    @Log(title = "问卷调查员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcSurveyor surveyor)
    {
        surveyor.setUpdateBy(getUsername());
        return toAjax(surveyorService.updateSurveyor(surveyor));
    }

    /**
     * 删除问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:remove')")
    @Log(title = "问卷调查员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{surveyorId}")
    public AjaxResult remove(@PathVariable Long surveyorId)
    {
        if (surveyorService.checkSurveyorExistResident(surveyorId))
        {
            return warn("该调查员已有关联居民,不允许删除");
        }

        return toAjax(surveyorService.deleteSurveyorById(surveyorId));
    }
}
