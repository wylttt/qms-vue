package com.javaxiaobear.module.gc.service;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;

/**
 * 问卷模板Service接口
 * 
 * @author javaxiaobear
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
     * 查询启用的问卷模板列表（用于前端下拉选择）
     * 
     * @return 启用的问卷模板集合
     */
    List<GcQuestionnaireTemplate> selectActiveTemplates();
    
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
     * 批量删除问卷模板
     * 
     * @param templateIds 模板ID数组
     * @return 结果
     */
    int deleteTemplateByIds(Long[] templateIds);
    
    /**
     * 校验模板编码唯一性
     * 
     * @param templateCode 模板编码
     * @return true唯一 false不唯一
     */
    boolean checkTemplateCodeUnique(String templateCode);
}
