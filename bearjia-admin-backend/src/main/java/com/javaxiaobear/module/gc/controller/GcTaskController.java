package com.javaxiaobear.module.gc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcTask;
import com.javaxiaobear.module.gc.domain.vo.TaskVO;
import com.javaxiaobear.module.gc.domain.vo.TaskProgressVO;
import com.javaxiaobear.module.gc.service.IGcTaskService;

/**
 * 任务Controller
 * 
 * @author javaxiaobear
 */
@RestController
@RequestMapping("/api/gc/task")
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
        List<TaskVO> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }
    
    /**
     * 查询任务详情
     */
    @PreAuthorize("@ss.hasPermi('gc:task:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId) {
        return success(taskService.selectTaskById(taskId));
    }
    
    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GcTask task) {
        return toAjax(taskService.insertTask(task));
    }
    
    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GcTask task) {
        return toAjax(taskService.updateTask(task));
    }
    
    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskId}")
    public AjaxResult remove(@PathVariable Long taskId) {
        return toAjax(taskService.deleteTaskById(taskId));
    }
    
    /**
     * 批量删除任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{taskIds}")
    public AjaxResult batchRemove(@PathVariable Long[] taskIds) {
        return toAjax(taskService.deleteTaskByIds(taskIds));
    }
    
    /**
     * 发布任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:publish')")
    @Log(title = "任务管理-发布", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{taskId}")
    public AjaxResult publish(@PathVariable Long taskId) {
        return toAjax(taskService.publishTask(taskId));
    }
    
    /**
     * 结束任务
     */
    @PreAuthorize("@ss.hasPermi('gc:task:finish')")
    @Log(title = "任务管理-结束", businessType = BusinessType.UPDATE)
    @PutMapping("/finish/{taskId}")
    public AjaxResult finish(@PathVariable Long taskId) {
        return toAjax(taskService.finishTask(taskId));
    }
    
    /**
     * 查询任务进度
     */
    @PreAuthorize("@ss.hasPermi('gc:task:query')")
    @GetMapping("/progress/{taskId}")
    public AjaxResult getProgress(@PathVariable Long taskId) {
        TaskProgressVO progress = taskService.selectTaskProgress(taskId);
        return success(progress);
    }
    
    /**
     * 验证任务编号唯一性
     */
    @GetMapping("/checkCode")
    public AjaxResult checkCode(@RequestParam String taskCode) {
        boolean unique = taskService.checkTaskCodeUnique(taskCode);
        return success(unique);
    }
}
