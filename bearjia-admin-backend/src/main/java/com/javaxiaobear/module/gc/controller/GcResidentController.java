package com.javaxiaobear.module.gc.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import com.javaxiaobear.module.gc.service.IGcResidentService;

/**
 * 居民信息Controller
 * 
 * @author javaxiaobear
 */
@RestController
@RequestMapping("/api/gc/resident")
public class GcResidentController extends BaseController {
    
    @Autowired
    private IGcResidentService residentService;
    
    /**
     * 查询居民列表
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcResident resident) {
        startPage();
        List<ResidentVO> list = residentService.selectResidentList(resident);
        return getDataTable(list);
    }
    
    /**
     * 查询居民详情
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:query')")
    @GetMapping("/{residentId}")
    public AjaxResult getInfo(@PathVariable Long residentId) {
        return success(residentService.selectResidentById(residentId));
    }
    
    /**
     * 新增居民
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:add')")
    @Log(title = "居民管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GcResident resident) {
        return toAjax(residentService.insertResident(resident));
    }
    
    /**
     * 修改居民
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:edit')")
    @Log(title = "居民管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GcResident resident) {
        return toAjax(residentService.updateResident(resident));
    }
    
    /**
     * 删除居民
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:remove')")
    @Log(title = "居民管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{residentId}")
    public AjaxResult remove(@PathVariable Long residentId) {
        return toAjax(residentService.deleteResidentById(residentId));
    }
    
    /**
     * 批量删除居民
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:remove')")
    @Log(title = "居民管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{residentIds}")
    public AjaxResult batchRemove(@PathVariable Long[] residentIds) {
        return toAjax(residentService.deleteResidentByIds(residentIds));
    }
    
    /**
     * 验证身份证号唯一性
     */
    @GetMapping("/checkIdCard")
    public AjaxResult checkIdCard(@RequestParam String idCardNo) {
        boolean unique = residentService.checkIdCardUnique(idCardNo);
        return success(unique);
    }
    
    /**
     * 批量导入居民
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:import')")
    @Log(title = "居民管理", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importResidents(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "surveyorId", required = false) Long surveyorId) {
        Map<String, Object> result = residentService.importResidents(file, surveyorId);
        return success(result);
    }
    
    /**
     * 导出居民列表
     */
    @PreAuthorize("@ss.hasPermi('gc:resident:export')")
    @Log(title = "居民管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GcResident resident) {
        residentService.exportResidents(resident, response);
    }
}
