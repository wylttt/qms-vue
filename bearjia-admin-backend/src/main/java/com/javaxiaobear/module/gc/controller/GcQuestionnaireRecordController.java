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
import org.springframework.web.bind.annotation.RestController;
import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.vo.QuestionnaireRecordVO;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireRecordService;

/**
 * 问卷记录Controller
 * 
 * @author javaxiaobear
 */
@RestController
@RequestMapping("/api/gc/questionnaire/record")
public class GcQuestionnaireRecordController extends BaseController {
    
    @Autowired
    private IGcQuestionnaireRecordService recordService;
    
    /**
     * 查询问卷记录列表
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcQuestionnaireRecord record) {
        startPage();
        List<QuestionnaireRecordVO> list = recordService.selectRecordList(record);
        return getDataTable(list);
    }
    
    /**
     * 查询问卷记录详情
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId) {
        return success(recordService.selectRecordById(recordId));
    }
    
    /**
     * 提交问卷（核心接口）
     * 包含：计算总分、判定重点人群、更新居民表、保存问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:submit')")
    @Log(title = "问卷提交", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submitQuestionnaire(@RequestBody GcQuestionnaireRecord record) {
        return toAjax(recordService.submitQuestionnaire(record));
    }
    
    /**
     * 修改问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:edit')")
    @Log(title = "问卷记录管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GcQuestionnaireRecord record) {
        return toAjax(recordService.updateRecord(record));
    }
    
    /**
     * 删除问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:remove')")
    @Log(title = "问卷记录管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordId}")
    public AjaxResult remove(@PathVariable Long recordId) {
        return toAjax(recordService.deleteRecordById(recordId));
    }
    
    /**
     * 批量删除问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:questionnaire:record:remove')")
    @Log(title = "问卷记录管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{recordIds}")
    public AjaxResult batchRemove(@PathVariable Long[] recordIds) {
        return toAjax(recordService.deleteRecordByIds(recordIds));
    }
}
