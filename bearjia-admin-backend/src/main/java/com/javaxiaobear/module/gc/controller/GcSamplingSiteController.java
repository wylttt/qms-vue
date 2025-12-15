package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import com.javaxiaobear.module.gc.service.IGcSamplingSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 采血点Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/api/gc/sampling-site")
public class GcSamplingSiteController extends BaseController {
    
    @Autowired
    private IGcSamplingSiteService gcSamplingSiteService;
    
    /**
     * 查询采血点列表
     */
    @PreAuthorize("@ss.hasPermi('gc:site:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcSamplingSite gcSamplingSite) {
        startPage();
        List<GcSamplingSite> list = gcSamplingSiteService.selectGcSamplingSiteList(gcSamplingSite);
        return getDataTable(list);
    }
    
    /**
     * 查询采血点下拉列表（不分页）
     */
    @GetMapping("/options")
    public AjaxResult getOptions(@RequestParam(required = false) Long regionId) {
        GcSamplingSite query = new GcSamplingSite();
        query.setRegionId(regionId);
        query.setStatus("0"); // 只查询正常状态的
        List<GcSamplingSite> list = gcSamplingSiteService.selectGcSamplingSiteList(query);
        return AjaxResult.success(list);
    }
    
    /**
     * 获取采血点详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:site:query')")
    @GetMapping(value = "/{siteId}")
    public AjaxResult getInfo(@PathVariable("siteId") Long siteId) {
        return AjaxResult.success(gcSamplingSiteService.selectGcSamplingSiteBySiteId(siteId));
    }
    
    /**
     * 新增采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcSamplingSite gcSamplingSite) {
        return toAjax(gcSamplingSiteService.insertGcSamplingSite(gcSamplingSite));
    }
    
    /**
     * 修改采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcSamplingSite gcSamplingSite) {
        return toAjax(gcSamplingSiteService.updateGcSamplingSite(gcSamplingSite));
    }
    
    /**
     * 删除采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:remove')")
    @DeleteMapping("/{siteIds}")
    public AjaxResult remove(@PathVariable Long[] siteIds) {
        return toAjax(gcSamplingSiteService.deleteGcSamplingSiteBySiteIds(siteIds));
    }
}
