package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcQuestionnaireTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷模板Mapper接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Mapper
public interface GcQuestionnaireTemplateMapper {
    
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
     * 根据模板编码查询
     * 
     * @param templateCode 模板编码
     * @return 问卷模板
     */
    GcQuestionnaireTemplate selectTemplateByCode(String templateCode);

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
     * 检查模板是否被问卷记录引用
     * 
     * @param templateId 模板ID
     * @return 引用数量
     */
    int checkTemplateExistRecord(@Param("templateId") Long templateId);
}
