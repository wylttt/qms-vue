package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;

/**
 * 问卷模板Mapper接口
 * 
 * @author javaxiaobear
 */
public interface GcQuestionnaireTemplateMapper {
    
    /**
     * 查询问卷模板列表
     * 
     * @param template 问卷模板
     * @return 问卷模板集合
     */
    List<GcQuestionnaireTemplate> selectTemplateList(GcQuestionnaireTemplate template);
    
    /**
     * 根据模板ID查询问卷模板
     * 
     * @param templateId 模板ID
     * @return 问卷模板
     */
    GcQuestionnaireTemplate selectTemplateById(Long templateId);
    
    /**
     * 根据模板编码查询问卷模板
     * 
     * @param templateCode 模板编码
     * @return 问卷模板
     */
    GcQuestionnaireTemplate selectTemplateByCode(String templateCode);
    
    /**
     * 查询当前启用的问卷模板
     * 
     * @return 问卷模板
     */
    GcQuestionnaireTemplate selectActiveTemplate();
    
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
     * 检查模板是否被使用
     * 
     * @param templateId 模板ID
     * @return 使用记录数
     */
    int checkTemplateInUse(Long templateId);
}
