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
import com.javaxiaobear.module.gc.domain.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireRecordService;

/**
 * 问卷记录Controller
 * 
 * @author Bear
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/gc/questionnaire/record")
public class GcQuestionnaireRecordController extends BaseController {
    
    @Autowired
    private IGcQuestionnaireRecordService recordService;

    /**
     * 查询问卷记录列表
     */
    @PreAuthorize("@ss.hasPermi('gc:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(GcQuestionnaireRecord record) {
        startPage();
        List<GcQuestionnaireRecord> list = recordService.selectRecordList(record);
        return getDataTable(list);
    }

    /**
     * 获取问卷记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('gc:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        return success(recordService.selectRecordById(recordId));
    }

    /**
     * 新增问卷记录(提交问卷,自动计算风险评分和判定重点人群)
     */
    @PreAuthorize("@ss.hasPermi('gc:record:add')")
    @Log(title = "问卷记录管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GcQuestionnaireRecord record) {
        record.setCreateBy(getUsername());
        int result = recordService.insertRecord(record);
        
        // 返回计算后的风险评分和重点人群判定结果
        return AjaxResult.success()
                .put("riskScore", record.getRiskScore())
                .put("isFocusGroup", record.getIsFocusGroup());
    }

    /**
     * 修改问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:record:edit')")
    @Log(title = "问卷记录管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GcQuestionnaireRecord record) {
        record.setUpdateBy(getUsername());
        int result = recordService.updateRecord(record);
        
        // 返回重新计算后的风险评分和重点人群判定结果
        return AjaxResult.success()
                .put("riskScore", record.getRiskScore())
                .put("isFocusGroup", record.getIsFocusGroup());
    }

    /**
     * 删除问卷记录
     */
    @PreAuthorize("@ss.hasPermi('gc:record:remove')")
    @Log(title = "问卷记录管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(recordService.deleteRecordByIds(recordIds));
    }

    /**
     * 统计问卷完成数量
     */
    @PreAuthorize("@ss.hasPermi('gc:record:stat')")
    @GetMapping("/count")
    public AjaxResult count(GcQuestionnaireRecord record) {
        int total = recordService.countRecord(record);
        return success(total);
    }
}
