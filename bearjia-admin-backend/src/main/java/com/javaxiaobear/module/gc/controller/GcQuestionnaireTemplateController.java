package com.javaxiaobear.module.gc.controller;

import java.util.List;
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
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireTemplateService;

/**
 * 问卷模板Controller
 * 
 * @author javaxiaobear
 */
@RestController
@RequestMapping("/api/gc/questionnaire/template")
public class GcQuestionnaireTemplateController extends BaseController {
    
    @Autowired
    private IGcQuestionnaireTemplateService templateService;
    
    /**
     * 查询问卷模板列表
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcQuestionnaireTemplate template) {
        startPage();
        List<GcQuestionnaireTemplate> list = templateService.selectTemplateList(template);
        return getDataTable(list);
    }
    
    /**
     * 查询问卷模板详情
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId) {
        return success(templateService.selectTemplateById(templateId));
    }
    
    /**
     * 查询启用的问卷模板列表（用于前端下拉选择）
     */
    @GetMapping("/active")
    public AjaxResult getActiveTemplates() {
        List<GcQuestionnaireTemplate> list = templateService.selectActiveTemplates();
        return success(list);
    }
    
    /**
     * 新增问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:add')")
    @Log(title = "问卷模板管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GcQuestionnaireTemplate template) {
        return toAjax(templateService.insertTemplate(template));
    }
    
    /**
     * 修改问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:edit')")
    @Log(title = "问卷模板管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GcQuestionnaireTemplate template) {
        return toAjax(templateService.updateTemplate(template));
    }
    
    /**
     * 删除问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:remove')")
    @Log(title = "问卷模板管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateId}")
    public AjaxResult remove(@PathVariable Long templateId) {
        return toAjax(templateService.deleteTemplateById(templateId));
    }
    
    /**
     * 批量删除问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:template:remove')")
    @Log(title = "问卷模板管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{templateIds}")
    public AjaxResult batchRemove(@PathVariable Long[] templateIds) {
        return toAjax(templateService.deleteTemplateByIds(templateIds));
    }
    
    /**
     * 验证模板编码唯一性
     */
    @GetMapping("/checkCode")
    public AjaxResult checkCode(@RequestParam String templateCode) {
        boolean unique = templateService.checkTemplateCodeUnique(templateCode);
        return success(unique);
    }
}
