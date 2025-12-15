package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcQuestionnaireTemplate;

import java.util.List;

/**
 * 问卷模板Service接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
public interface IGcQuestionnaireTemplateService {
    
    /**
     * 查询问卷模板列表
     * 
     * @param template 问卷模板
     * @return 问卷模板集合
     */
    List<GcQuestionnaireTemplate> selectTemplateList(GcQuestionnaireTemplate template);

    /**
     * 查询问卷模板详情
     * 
     * @param templateId 模板ID
     * @return 问卷模板
     */
    GcQuestionnaireTemplate selectTemplateById(Long templateId);

    /**
     * 查询启用的问卷模板列表
     * 
     * @return 问卷模板列表
     */
    List<GcQuestionnaireTemplate> selectEnabledTemplates();

    /**
     * 新增问卷模板
     * 
     * @param template 问卷模板
     * @return 结果
     */
    int insertTemplate(GcQuestionnaireTemplate template);

    /**
     * 修改问卷模板
     * 
     * @param template 问卷模板
     * @return 结果
     */
    int updateTemplate(GcQuestionnaireTemplate template);

    /**
     * 删除问卷模板
     * 
     * @param templateId 模板ID
     * @return 结果
     */
    int deleteTemplateById(Long templateId);

    /**
     * 检查模板编码唯一性
     * 
     * @param template 问卷模板
     * @return 结果
     */
    boolean checkTemplateCodeUnique(GcQuestionnaireTemplate template);

    /**
     * 检查模板是否被问卷记录引用
     * 
     * @param templateId 模板ID
     * @return 结果
     */
    boolean checkTemplateExistRecord(Long templateId);
}
