package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcPushLog;
import com.javaxiaobear.module.gc.service.IGcPushLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推送日志Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Api(tags = "推送日志管理")
@RestController
@RequestMapping("/gc/pushlog")
public class GcPushLogController extends BaseController {
    
    @Autowired
    private IGcPushLogService pushLogService;
    
    /**
     * 查询推送日志列表
     */
    @ApiOperation("查询推送日志列表")
    @PreAuthorize("@ss.hasPermi('gc:pushlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcPushLog pushLog) {
        startPage();
        List<GcPushLog> list = pushLogService.selectPushLogList(pushLog);
        return getDataTable(list);
    }
    
    /**
     * 获取推送日志详细信息
     */
    @ApiOperation("获取推送日志详细信息")
    @PreAuthorize("@ss.hasPermi('gc:pushlog:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(
        @ApiParam(value = "日志ID", required = true) @PathVariable("logId") Long logId
    ) {
        return success(pushLogService.selectPushLogById(logId));
    }
    
    /**
     * 删除推送日志
     */
    @ApiOperation("删除推送日志")
    @PreAuthorize("@ss.hasPermi('gc:pushlog:remove')")
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(
        @ApiParam(value = "日志ID数组", required = true) @PathVariable Long[] logIds
    ) {
        return toAjax(pushLogService.deletePushLogByIds(logIds));
    }
    
    /**
     * 根据预约ID查询推送日志
     */
    @ApiOperation("根据预约ID查询推送日志")
    @PreAuthorize("@ss.hasPermi('gc:pushlog:query')")
    @GetMapping("/appointment/{appointmentId}")
    public AjaxResult getByAppointmentId(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        List<GcPushLog> list = pushLogService.selectPushLogByAppointmentId(appointmentId);
        return success(list);
    }
    
    /**
     * 查询最近的推送日志
     */
    @ApiOperation("查询最近的推送日志")
    @PreAuthorize("@ss.hasPermi('gc:pushlog:query')")
    @GetMapping("/latest/{appointmentId}")
    public AjaxResult getLatest(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        return success(pushLogService.selectLatestPushLog(appointmentId));
    }
}
