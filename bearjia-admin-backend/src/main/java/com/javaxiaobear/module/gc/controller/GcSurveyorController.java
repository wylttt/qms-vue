package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcSurveyor;
import com.javaxiaobear.module.gc.service.IGcSurveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 问卷调查员Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/api/gc/surveyor")
public class GcSurveyorController extends BaseController {
    
    @Autowired
    private IGcSurveyorService gcSurveyorService;

    /**
     * 查询问卷调查员列表
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcSurveyor gcSurveyor) {
        startPage();
        List<GcSurveyor> list = gcSurveyorService.selectGcSurveyorList(gcSurveyor);
        return getDataTable(list);
    }

    /**
     * 查询问卷调查员下拉列表（不分页）
     */
    @GetMapping("/options")
    public AjaxResult getOptions(@RequestParam(required = false) Long regionId,
                                  @RequestParam(required = false) Long communityId,
                                  @RequestParam(required = false) Long siteId) {
        GcSurveyor query = new GcSurveyor();
        query.setRegionId(regionId);
        query.setCommunityId(communityId);
        query.setSiteId(siteId);
        query.setStatus("0"); // 只查询正常状态的
        List<GcSurveyor> list = gcSurveyorService.selectGcSurveyorList(query);
        return AjaxResult.success(list);
    }

    /**
     * 获取问卷调查员详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:query')")
    @GetMapping(value = "/{surveyorId}")
    public AjaxResult getInfo(@PathVariable("surveyorId") Long surveyorId) {
        return AjaxResult.success(gcSurveyorService.selectGcSurveyorBySurveyorId(surveyorId));
    }

    /**
     * 新增问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcSurveyor gcSurveyor) {
        // 检查身份证号唯一性
        if (!gcSurveyorService.checkIdCardUnique(gcSurveyor.getIdCard(), null)) {
            return AjaxResult.error("身份证号已存在");
        }
        return toAjax(gcSurveyorService.insertGcSurveyor(gcSurveyor));
    }

    /**
     * 修改问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcSurveyor gcSurveyor) {
        // 检查身份证号唯一性
        if (!gcSurveyorService.checkIdCardUnique(gcSurveyor.getIdCard(), gcSurveyor.getSurveyorId())) {
            return AjaxResult.error("身份证号已存在");
        }
        return toAjax(gcSurveyorService.updateGcSurveyor(gcSurveyor));
    }

    /**
     * 删除问卷调查员
     */
    @PreAuthorize("@ss.hasPermi('gc:surveyor:remove')")
    @DeleteMapping("/{surveyorIds}")
    public AjaxResult remove(@PathVariable Long[] surveyorIds) {
        return toAjax(gcSurveyorService.deleteGcSurveyorBySurveyorIds(surveyorIds));
    }

    /**
     * 检查身份证号是否唯一
     */
    @GetMapping("/checkIdCardUnique")
    public AjaxResult checkIdCardUnique(@RequestParam String idCard,
                                         @RequestParam(required = false) Long surveyorId) {
        boolean unique = gcSurveyorService.checkIdCardUnique(idCard, surveyorId);
        return AjaxResult.success(unique);
    }
}
