package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.GcPushLog;
import com.javaxiaobear.module.gc.service.IGcBloodAppointmentService;
import com.javaxiaobear.module.gc.service.IGcPushLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采血预约Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Api(tags = "采血预约管理")
@RestController
@RequestMapping("/gc/appointment")
public class GcBloodAppointmentController extends BaseController {
    
    @Autowired
    private IGcBloodAppointmentService appointmentService;
    
    @Autowired
    private IGcPushLogService pushLogService;
    
    /**
     * 查询采血预约列表
     */
    @ApiOperation("查询采血预约列表")
    @PreAuthorize("@ss.hasPermi('gc:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcBloodAppointment appointment) {
        startPage();
        List<GcBloodAppointment> list = appointmentService.selectAppointmentList(appointment);
        return getDataTable(list);
    }
    
    /**
     * 获取采血预约详细信息
     */
    @ApiOperation("获取采血预约详细信息")
    @PreAuthorize("@ss.hasPermi('gc:appointment:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        return success(appointmentService.selectAppointmentById(appointmentId));
    }
    
    /**
     * 新增采血预约
     */
    @ApiOperation("新增采血预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcBloodAppointment appointment) {
        return toAjax(appointmentService.insertAppointment(appointment));
    }
    
    /**
     * 修改采血预约
     */
    @ApiOperation("修改采血预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcBloodAppointment appointment) {
        return toAjax(appointmentService.updateAppointment(appointment));
    }
    
    /**
     * 删除采血预约
     */
    @ApiOperation("删除采血预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:remove')")
    @DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(
        @ApiParam(value = "预约ID数组", required = true) @PathVariable Long[] appointmentIds
    ) {
        return toAjax(appointmentService.deleteAppointmentByIds(appointmentIds));
    }
    
    /**
     * 根据居民ID查询预约记录
     */
    @ApiOperation("根据居民ID查询预约记录")
    @PreAuthorize("@ss.hasPermi('gc:appointment:query')")
    @GetMapping("/resident/{residentId}")
    public AjaxResult getByResidentId(
        @ApiParam(value = "居民ID", required = true) @PathVariable("residentId") Long residentId
    ) {
        List<GcBloodAppointment> list = appointmentService.selectAppointmentByResidentId(residentId);
        return success(list);
    }
    
    /**
     * 根据采血点ID查询预约记录
     */
    @ApiOperation("根据采血点ID查询预约记录")
    @PreAuthorize("@ss.hasPermi('gc:appointment:query')")
    @GetMapping("/site/{siteId}")
    public AjaxResult getBySiteId(
        @ApiParam(value = "采血点ID", required = true) @PathVariable("siteId") Long siteId
    ) {
        List<GcBloodAppointment> list = appointmentService.selectAppointmentBySiteId(siteId);
        return success(list);
    }
    
    /**
     * 取消预约
     */
    @ApiOperation("取消预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:edit')")
    @PutMapping("/cancel/{appointmentId}")
    public AjaxResult cancel(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        return toAjax(appointmentService.cancelAppointment(appointmentId));
    }
    
    /**
     * 推送预约到第三方系统
     */
    @ApiOperation("推送预约到第三方系统")
    @PreAuthorize("@ss.hasPermi('gc:appointment:push')")
    @PostMapping("/push/{appointmentId}")
    public AjaxResult push(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        boolean result = appointmentService.pushToThirdSystem(appointmentId);
        return result ? success("推送成功") : error("推送失败");
    }
    
    /**
     * 批量推送待推送的预约
     */
    @ApiOperation("批量推送待推送的预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:push')")
    @PostMapping("/push/batch")
    public AjaxResult batchPush() {
        int count = appointmentService.batchPushPendingAppointments();
        return success("推送完成，成功推送" + count + "条记录");
    }
    
    /**
     * 重试推送失败的预约
     */
    @ApiOperation("重试推送失败的预约")
    @PreAuthorize("@ss.hasPermi('gc:appointment:push')")
    @PostMapping("/push/retry")
    public AjaxResult retryPush() {
        int count = appointmentService.retryFailedAppointments();
        return success("重试完成，成功推送" + count + "条记录");
    }
    
    /**
     * 接收第三方系统回调
     * 
     * 此接口由第三方采血系统调用，用于回传采样状态
     */
    @ApiOperation("接收第三方系统回调")
    @PostMapping("/callback")
    public AjaxResult callback(
        @ApiParam(value = "第三方系统预约ID", required = true) @RequestParam String thirdSystemId,
        @ApiParam(value = "采样状态(0未采样/1已采样)", required = true) @RequestParam String samplingStatus,
        @ApiParam(value = "条码编号") @RequestParam(required = false) String barcodeNumber
    ) {
        boolean result = appointmentService.receiveThirdSystemCallback(thirdSystemId, samplingStatus, barcodeNumber);
        return result ? success("回调处理成功") : error("回调处理失败");
    }
    
    /**
     * 查询预约的推送日志
     */
    @ApiOperation("查询预约的推送日志")
    @PreAuthorize("@ss.hasPermi('gc:appointment:query')")
    @GetMapping("/logs/{appointmentId}")
    public AjaxResult getPushLogs(
        @ApiParam(value = "预约ID", required = true) @PathVariable("appointmentId") Long appointmentId
    ) {
        List<GcPushLog> logs = pushLogService.selectPushLogByAppointmentId(appointmentId);
        return success(logs);
    }
}
