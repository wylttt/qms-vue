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
import com.javaxiaobear.module.gc.domain.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireTemplateService;

/**
 * 问卷模板Controller
 * 
 * @author Bear
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/gc/questionnaire/template")
public class GcQuestionnaireTemplateController extends BaseController {
    
    @Autowired
    private IGcQuestionnaireTemplateService templateService;

    /**
     * 查询问卷模板列表
     */
    @PreAuthorize("@ss.hasPermi('gc:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcQuestionnaireTemplate template) {
        startPage();
        List<GcQuestionnaireTemplate> list = templateService.selectTemplateList(template);
        return getDataTable(list);
    }

    /**
     * 查询启用的问卷模板列表(供下拉选择使用)
     */
    @PreAuthorize("@ss.hasPermi('gc:template:list')")
    @GetMapping("/enabled")
    public AjaxResult listEnabled() {
        List<GcQuestionnaireTemplate> list = templateService.selectEnabledTemplates();
        return success(list);
    }

    /**
     * 获取问卷模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return success(templateService.selectTemplateById(templateId));
    }

    /**
     * 新增问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:template:add')")
    @Log(title = "问卷模板管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcQuestionnaireTemplate template) {
        if (!templateService.checkTemplateCodeUnique(template)) {
            return error("新增问卷模板失败,模板编码已存在");
        }
        template.setCreateBy(getUsername());
        return toAjax(templateService.insertTemplate(template));
    }

    /**
     * 修改问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:template:edit')")
    @Log(title = "问卷模板管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcQuestionnaireTemplate template) {
        template.setUpdateBy(getUsername());
        return toAjax(templateService.updateTemplate(template));
    }

    /**
     * 删除问卷模板
     */
    @PreAuthorize("@ss.hasPermi('gc:template:remove')")
    @Log(title = "问卷模板管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateId}")
    public AjaxResult remove(@PathVariable Long templateId) {
        if (templateService.checkTemplateExistRecord(templateId)) {
            return warn("该问卷模板已被使用,不允许删除");
        }
        return toAjax(templateService.deleteTemplateById(templateId));
    }
}
