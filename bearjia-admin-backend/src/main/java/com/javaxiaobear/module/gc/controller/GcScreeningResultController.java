package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningResultVO;
import com.javaxiaobear.module.gc.service.IGcScreeningResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 筛查结果Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/screening/result")
public class GcScreeningResultController extends BaseController {

    @Autowired
    private IGcScreeningResultService screeningResultService;

    /**
     * 查询筛查结果列表
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcScreeningResult result) {
        startPage();
        List<ScreeningResultVO> list = screeningResultService.selectScreeningResultList(result);
        return getDataTable(list);
    }

    /**
     * 查询筛查结果详情
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:query')")
    @GetMapping("/{resultId}")
    public AjaxResult getInfo(@PathVariable("resultId") Long resultId) {
        return success(screeningResultService.selectScreeningResultDetail(resultId));
    }

    /**
     * 新增筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcScreeningResult result) {
        return toAjax(screeningResultService.insertScreeningResult(result));
    }

    /**
     * 修改筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcScreeningResult result) {
        return toAjax(screeningResultService.updateScreeningResult(result));
    }

    /**
     * 删除筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:remove')")
    @DeleteMapping("/{resultId}")
    public AjaxResult remove(@PathVariable Long resultId) {
        return toAjax(screeningResultService.deleteScreeningResultById(resultId));
    }

    /**
     * 批量删除筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:remove')")
    @DeleteMapping("/batch/{resultIds}")
    public AjaxResult removeBatch(@PathVariable Long[] resultIds) {
        return toAjax(screeningResultService.deleteScreeningResultByIds(resultIds));
    }

    /**
     * 审核筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:review')")
    @PutMapping("/review/{resultId}")
    public AjaxResult review(@PathVariable Long resultId) {
        // TODO: 获取当前登录用户信息
        Long reviewUserId = 1L;
        String reviewUserName = "系统管理员";
        return toAjax(screeningResultService.reviewScreeningResult(resultId, reviewUserId, reviewUserName));
    }

    /**
     * 根据居民ID查询筛查结果列表
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:query')")
    @GetMapping("/resident/{residentId}")
    public AjaxResult getByResidentId(@PathVariable Long residentId) {
        List<ScreeningResultVO> list = screeningResultService.selectByResidentId(residentId);
        return success(list);
    }

    /**
     * 统计各风险等级人数
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:statistics')")
    @GetMapping("/statistics/riskLevel")
    public AjaxResult statisticsRiskLevel(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer regionLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> statistics = screeningResultService.countByRiskLevel(
            regionId, regionLevel, startDate, endDate
        );
        return success(statistics);
    }

    /**
     * 导出筛查结果列表
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:export')")
    @PostMapping("/export")
    public void export(GcScreeningResult result) {
        List<ScreeningResultVO> list = screeningResultService.exportScreeningResultList(result);
        // TODO: 实现Excel导出逻辑
        // ExcelUtil<ScreeningResultVO> util = new ExcelUtil<>(ScreeningResultVO.class);
        // util.exportExcel(response, list, "筛查结果数据");
    }
}
