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
import com.javaxiaobear.module.gc.domain.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;
import com.javaxiaobear.module.gc.service.IGcTaskService;

/**
 * 任务管理Controller
 * 
 * @author Bear
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/gc/task")
public class GcTaskController extends BaseController {
    
    @Autowired
    private IGcTaskService taskService;

    /**
     * 查询任务列表
     */
    @PreAuthorize("@ss.hasPermi('gc:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcTask task) {
        startPage();
        List<GcTask> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    /**
     * 获取任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId) {
        return success(taskService.selectTaskById(taskId));
    }

    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcTask task) {
        if (!taskService.checkTaskCodeUnique(task)) {
            return error("新增任务失败,任务编号已存在");
        }
        task.setCreateBy(getUsername());
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcTask task) {
        task.setUpdateBy(getUsername());
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds) {
        return toAjax(taskService.deleteTaskByIds(taskIds));
    }

    /**
     * 查询任务进度统计
     */
    @PreAuthorize("@ss.hasPermi('gc:task:progress')")
    @GetMapping("/progress")
    public TableDataInfo progress(GcTask task) {
        startPage();
        List<TaskProgressVO> list = taskService.selectTaskProgress(task);
        return getDataTable(list);
    }

    /**
     * 根据区域ID查询任务进度统计(含下级区域汇总)
     */
    @PreAuthorize("@ss.hasPermi('gc:task:progress')")
    @GetMapping("/progress/region")
    public AjaxResult progressByRegion(@RequestParam("regionId") Long regionId,
                                       @RequestParam(value = "taskType", required = false) String taskType) {
        List<TaskProgressVO> list = taskService.selectTaskProgressByRegion(regionId, taskType);
        return success(list);
    }
}
