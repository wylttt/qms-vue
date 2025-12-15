package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.R;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcFollowUp;
import com.javaxiaobear.module.gc.service.IGcFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访对象Controller
 *
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/gc/followup")
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
        List<GcFollowUp> list = followUpService.selectFollowUpList(followUp);
        return getDataTable(list);
    }

    /**
     * 查询随访对象详情列表（包含居民和筛查结果信息）
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:list')")
    @GetMapping("/list/detail")
    public TableDataInfo listDetail(GcFollowUp followUp) {
        startPage();
        List<GcFollowUp> list = followUpService.selectFollowUpDetailList(followUp);
        return getDataTable(list);
    }

    /**
     * 获取随访对象详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:query')")
    @GetMapping(value = "/{followUpId}")
    public R<GcFollowUp> getInfo(@PathVariable("followUpId") Long followUpId) {
        return R.ok(followUpService.selectFollowUpById(followUpId));
    }

    /**
     * 根据居民ID获取随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:query')")
    @GetMapping(value = "/resident/{residentId}")
    public R<List<GcFollowUp>> getByResidentId(@PathVariable("residentId") Long residentId) {
        return R.ok(followUpService.selectFollowUpByResidentId(residentId));
    }

    /**
     * 新增随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:add')")
    @PostMapping
    public R<Void> add(@RequestBody GcFollowUp followUp) {
        return toAjax(followUpService.insertFollowUp(followUp));
    }

    /**
     * 修改随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody GcFollowUp followUp) {
        return toAjax(followUpService.updateFollowUp(followUp));
    }

    /**
     * 删除随访对象
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:remove')")
    @DeleteMapping("/{followUpIds}")
    public R<Void> remove(@PathVariable Long[] followUpIds) {
        return toAjax(followUpService.deleteFollowUpByIds(followUpIds));
    }

    /**
     * 分配随访任务
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:assign')")
    @PostMapping("/assign/{followUpId}")
    public R<Void> assign(@PathVariable Long followUpId,
                          @RequestParam Long assignedUserId,
                          @RequestParam String assignedUserName) {
        return toAjax(followUpService.assignFollowUp(followUpId, assignedUserId, assignedUserName));
    }

    /**
     * 完成随访
     */
    @PreAuthorize("@ss.hasPermi('gc:followup:complete')")
    @PutMapping("/complete/{followUpId}")
    public R<Void> complete(@PathVariable Long followUpId) {
        return toAjax(followUpService.completeFollowUp(followUpId));
    }
}
