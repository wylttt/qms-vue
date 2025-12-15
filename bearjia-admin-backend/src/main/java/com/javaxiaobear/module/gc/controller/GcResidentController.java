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
import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import com.javaxiaobear.module.gc.service.IGcResidentService;

/**
 * 居民信息Controller
 * 
 * @author Bear
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/gc/resident")
public class GcResidentController extends BaseController {
    
    @Autowired
    private IGcResidentService residentService;

    /**
     * 查询居民信息列表
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcResident resident) {
        startPage();
        List<ResidentVO> list = residentService.selectResidentList(resident);
        return getDataTable(list);
    }

    /**
     * 获取居民信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:query')")
    @GetMapping(value = "/{residentId}")
    public AjaxResult getInfo(@PathVariable("residentId") Long residentId) {
        return success(residentService.selectResidentById(residentId));
    }

    /**
     * 新增居民信息
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:add')")
    @Log(title = "居民信息管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcResident resident) {
        resident.setCreateBy(getUsername());
        return toAjax(residentService.insertResident(resident));
    }

    /**
     * 修改居民信息
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:edit')")
    @Log(title = "居民信息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcResident resident) {
        resident.setUpdateBy(getUsername());
        return toAjax(residentService.updateResident(resident));
    }

    /**
     * 删除居民信息
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:remove')")
    @Log(title = "居民信息管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{residentIds}")
    public AjaxResult remove(@PathVariable Long[] residentIds) {
        return toAjax(residentService.deleteResidentByIds(residentIds));
    }

    /**
     * 统计居民总数
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:stat')")
    @GetMapping("/count")
    public AjaxResult count(GcResident resident) {
        int total = residentService.countResident(resident);
        return success(total);
    }

    /**
     * 统计重点人群数量
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:stat')")
    @GetMapping("/count/focus")
    public AjaxResult countFocus(GcResident resident) {
        int total = residentService.countFocusGroup(resident);
        return success(total);
    }

    /**
     * 导出居民信息
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:export')")
    @Log(title = "居民信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GcResident resident) {
        List<ResidentVO> list = residentService.exportResident(resident);
        // TODO: 使用EasyExcel或POI导出Excel文件
        // ExcelUtil<ResidentVO> util = new ExcelUtil<>(ResidentVO.class);
        // util.exportExcel(response, list, "居民信息数据");
    }
}
