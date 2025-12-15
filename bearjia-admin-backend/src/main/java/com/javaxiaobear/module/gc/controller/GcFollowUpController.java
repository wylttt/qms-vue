package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcFollowUp;
import com.javaxiaobear.module.gc.service.IGcFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 随访对象Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/followup")
public class GcFollowUpController extends BaseController {

    @Autowired
    private IGcFollowUpService followUpService;

    /**
     * 查询随访对象列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcFollowUp followUp) {
        startPage();
        List<Map<String, Object>> list = followUpService.selectFollowUpList(followUp);
        return getDataTable(list);
    }

    /**
     * 查询随访对象详情
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:query')")
    @GetMapping("/{followUpId}")
    public AjaxResult getInfo(@PathVariable("followUpId") Long followUpId) {
        return success(followUpService.selectFollowUpDetail(followUpId));
    }

    /**
     * 新增随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcFollowUp followUp) {
        return toAjax(followUpService.insertFollowUp(followUp));
    }

    /**
     * 修改随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcFollowUp followUp) {
        return toAjax(followUpService.updateFollowUp(followUp));
    }

    /**
     * 删除随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:remove')")
    @DeleteMapping("/{followUpId}")
    public AjaxResult remove(@PathVariable Long followUpId) {
        return toAjax(followUpService.deleteFollowUpById(followUpId));
    }

    /**
     * 批量删除随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:remove')")
    @DeleteMapping("/batch/{followUpIds}")
    public AjaxResult removeBatch(@PathVariable Long[] followUpIds) {
        return toAjax(followUpService.deleteFollowUpByIds(followUpIds));
    }

    /**
     * 完成随访
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:complete')")
    @PutMapping("/complete/{followUpId}")
    public AjaxResult complete(@PathVariable Long followUpId,
                               @RequestParam(required = false) String followUpConclusion) {
        return toAjax(followUpService.completeFollowUp(followUpId, followUpConclusion));
    }

    /**
     * 根据居民ID查询随访对象列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:query')")
    @GetMapping("/resident/{residentId}")
    public AjaxResult getByResidentId(@PathVariable Long residentId) {
        List<Map<String, Object>> list = followUpService.selectByResidentId(residentId);
        return success(list);
    }

    /**
     * 查询待随访列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:pending')")
    @GetMapping("/pending")
    public TableDataInfo getPendingList() {
        startPage();
        List<Map<String, Object>> list = followUpService.selectPendingFollowUps();
        return getDataTable(list);
    }

    /**
     * 查询超期随访列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:overdue')")
    @GetMapping("/overdue")
    public TableDataInfo getOverdueList(@RequestParam(defaultValue = "7") int days) {
        startPage();
        List<Map<String, Object>> list = followUpService.selectOverdueFollowUps(days);
        return getDataTable(list);
    }

    /**
     * 导出随访对象列表
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:export')")
    @PostMapping("/export")
    public void export(GcFollowUp followUp) {
        List<Map<String, Object>> list = followUpService.selectFollowUpList(followUp);
        // TODO: 实现Excel导出逻辑
        // ExcelUtil<Map<String, Object>> util = new ExcelUtil<>(Map.class);
        // util.exportExcel(response, list, "随访对象数据");
    }
}
