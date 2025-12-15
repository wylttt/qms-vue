package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.R;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcScreeningResult;
import com.javaxiaobear.module.gc.domain.vo.ScreeningStatisticsVO;
import com.javaxiaobear.module.gc.service.IGcScreeningResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 筛查结果Controller
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/gc/screening/result")
public class GcScreeningResultController extends BaseController {
    @Autowired
    private IGcScreeningResultService screeningResultService;

    /**
     * 查询筛查结果列表
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcScreeningResult screeningResult) {
        startPage();
        List<GcScreeningResult> list = screeningResultService.selectScreeningResultList(screeningResult);
        return getDataTable(list);
    }

    /**
     * 获取筛查结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:query')")
    @GetMapping(value = "/{resultId}")
    public R<GcScreeningResult> getInfo(@PathVariable("resultId") Long resultId) {
        return R.ok(screeningResultService.selectScreeningResultById(resultId));
    }

    /**
     * 根据居民ID获取筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:query')")
    @GetMapping(value = "/resident/{residentId}")
    public R<GcScreeningResult> getByResidentId(@PathVariable("residentId") Long residentId) {
        return R.ok(screeningResultService.selectScreeningResultByResidentId(residentId));
    }

    /**
     * 新增筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:add')")
    @PostMapping
    public R<Void> add(@RequestBody GcScreeningResult screeningResult) {
        return toAjax(screeningResultService.insertScreeningResult(screeningResult));
    }

    /**
     * 修改筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody GcScreeningResult screeningResult) {
        return toAjax(screeningResultService.updateScreeningResult(screeningResult));
    }

    /**
     * 删除筛查结果
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:remove')")
    @DeleteMapping("/{resultIds}")
    public R<Void> remove(@PathVariable Long[] resultIds) {
        return toAjax(screeningResultService.deleteScreeningResultByIds(resultIds));
    }

    /**
     * 按采血点统计筛查数据
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:statistics')")
    @GetMapping("/statistics/site")
    public R<List<ScreeningStatisticsVO>> statisticsBySite(@RequestParam(required = false) Long siteId) {
        List<ScreeningStatisticsVO> list = screeningResultService.selectStatisticsBySite(siteId);
        return R.ok(list);
    }

    /**
     * 按行政区划统计筛查数据
     */
    @PreAuthorize("@ss.hasPermi('gc:screening:result:statistics')")
    @GetMapping("/statistics/district")
    public R<List<ScreeningStatisticsVO>> statisticsByDistrict(@RequestParam(required = false) Long districtId) {
        List<ScreeningStatisticsVO> list = screeningResultService.selectStatisticsByDistrict(districtId);
        return R.ok(list);
    }
}
