package com.javaxiaobear.module.gc.controller;

import com.javaxiaobear.common.core.controller.BaseController;
import com.javaxiaobear.common.core.domain.AjaxResult;
import com.javaxiaobear.common.core.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.vo.BloodAppointmentVO;
import com.javaxiaobear.module.gc.service.IGcBloodAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采血预约Controller
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@RestController
@RequestMapping("/api/gc/blood/appointment")
public class GcBloodAppointmentController extends BaseController {

    @Autowired
    private IGcBloodAppointmentService appointmentService;

    /**
     * 查询采血预约列表
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcBloodAppointment appointment) {
        startPage();
        List<BloodAppointmentVO> list = appointmentService.selectAppointmentList(appointment);
        return getDataTable(list);
    }

    /**
     * 查询采血预约详情
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:query')")
    @GetMapping("/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId) {
        return success(appointmentService.selectAppointmentDetail(appointmentId));
    }

    /**
     * 新增采血预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:add')")
    @PostMapping
    public AjaxResult add(@RequestBody GcBloodAppointment appointment) {
        return toAjax(appointmentService.insertAppointment(appointment));
    }

    /**
     * 修改采血预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody GcBloodAppointment appointment) {
        return toAjax(appointmentService.updateAppointment(appointment));
    }

    /**
     * 删除采血预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:remove')")
    @DeleteMapping("/{appointmentId}")
    public AjaxResult remove(@PathVariable Long appointmentId) {
        return toAjax(appointmentService.deleteAppointmentById(appointmentId));
    }

    /**
     * 批量删除采血预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:remove')")
    @DeleteMapping("/batch/{appointmentIds}")
    public AjaxResult removeBatch(@PathVariable Long[] appointmentIds) {
        return toAjax(appointmentService.deleteAppointmentByIds(appointmentIds));
    }

    /**
     * 确认预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:confirm')")
    @PutMapping("/confirm/{appointmentId}")
    public AjaxResult confirm(@PathVariable Long appointmentId) {
        // TODO: 获取当前登录用户信息
        Long operatorId = 1L;
        String operatorName = "系统管理员";
        return toAjax(appointmentService.confirmAppointment(appointmentId, operatorId, operatorName));
    }

    /**
     * 取消预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:cancel')")
    @PutMapping("/cancel/{appointmentId}")
    public AjaxResult cancel(@PathVariable Long appointmentId, @RequestParam String cancelReason) {
        // TODO: 获取当前登录用户信息
        Long operatorId = 1L;
        String operatorName = "系统管理员";
        return toAjax(appointmentService.cancelAppointment(appointmentId, cancelReason, operatorId, operatorName));
    }

    /**
     * 更新采样状态
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:sampling')")
    @PutMapping("/sampling/{appointmentId}")
    public AjaxResult updateSampling(@PathVariable Long appointmentId,
                                     @RequestParam String samplerName,
                                     @RequestParam String sampleCode) {
        return toAjax(appointmentService.updateSamplingStatus(appointmentId, samplerName, sampleCode));
    }

    /**
     * 批量预约
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:batch')")
    @PostMapping("/batch")
    public AjaxResult batchAppointment(@RequestParam List<Long> residentIds,
                                       @RequestParam Long appointmentSiteId,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate,
                                       @RequestParam String appointmentPeriod) {
        int successCount = appointmentService.batchAppointment(
            residentIds, appointmentSiteId, appointmentDate, appointmentPeriod
        );
        return success("成功预约" + successCount + "人");
    }

    /**
     * 导出预约列表
     */
    @PreAuthorize("@ss.hasPermi('gc:appointment:export')")
    @PostMapping("/export")
    public void export(GcBloodAppointment appointment) {
        List<BloodAppointmentVO> list = appointmentService.exportAppointmentList(appointment);
        // TODO: 实现Excel导出逻辑
        // ExcelUtil<BloodAppointmentVO> util = new ExcelUtil<>(BloodAppointmentVO.class);
        // util.exportExcel(response, list, "采血预约数据");
    }

    /**
     * 检查采血点容量
     */
    @GetMapping("/check-capacity")
    public AjaxResult checkCapacity(@RequestParam Long siteId,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate) {
        Map<String, Object> capacityInfo = appointmentService.checkCapacity(siteId, appointmentDate);
        return success(capacityInfo);
    }

    /**
     * 获取可用时间段
     */
    @GetMapping("/available-slots")
    public AjaxResult getAvailableSlots(@RequestParam Long siteId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date appointmentDate) {
        List<Map<String, Object>> slots = appointmentService.getAvailableTimeSlots(siteId, appointmentDate);
        return success(slots);
    }
}
