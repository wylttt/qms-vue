package com.javaxiaobear.module.gc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcSamplingSite;
import com.javaxiaobear.module.gc.service.IGcSamplingSiteService;

/**
 * 采血点Controller
 * 
 * @author javaxiaobear
 * @date 2025-12-15
 */
@RestController
@RequestMapping("/gc/sampling-site")
public class GcSamplingSiteController extends BaseController
{
    @Autowired
    private IGcSamplingSiteService samplingSiteService;

    /**
     * 查询采血点列表
     */
    @PreAuthorize("@ss.hasPermi('gc:site:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcSamplingSite samplingSite)
    {
        startPage();
        List<GcSamplingSite> list = samplingSiteService.selectSamplingSiteList(samplingSite);
        return getDataTable(list);
    }

    /**
     * 查询采血点下拉列表
     */
    @GetMapping("/options")
    public AjaxResult options(@RequestParam(value = "regionId", required = false) Long regionId)
    {
        List<GcSamplingSite> list = samplingSiteService.selectSamplingSiteOptions(regionId);
        return success(list);
    }

    /**
     * 获取采血点详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:site:query')")
    @GetMapping(value = "/{siteId}")
    public AjaxResult getInfo(@PathVariable("siteId") Long siteId)
    {
        return success(samplingSiteService.selectSamplingSiteById(siteId));
    }

    /**
     * 新增采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:add')")
    @Log(title = "采血点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcSamplingSite samplingSite)
    {
        samplingSite.setCreateBy(getUsername());
        return toAjax(samplingSiteService.insertSamplingSite(samplingSite));
    }

    /**
     * 修改采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:edit')")
    @Log(title = "采血点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcSamplingSite samplingSite)
    {
        samplingSite.setUpdateBy(getUsername());
        return toAjax(samplingSiteService.updateSamplingSite(samplingSite));
    }

    /**
     * 删除采血点
     */
    @PreAuthorize("@ss.hasPermi('gc:site:remove')")
    @Log(title = "采血点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{siteId}")
    public AjaxResult remove(@PathVariable Long siteId)
    {
        if (samplingSiteService.checkSiteExistResident(siteId))
        {
            return warn("该采血点已有居民预约,不允许删除");
        }

        return toAjax(samplingSiteService.deleteSamplingSiteById(siteId));
    }
}
