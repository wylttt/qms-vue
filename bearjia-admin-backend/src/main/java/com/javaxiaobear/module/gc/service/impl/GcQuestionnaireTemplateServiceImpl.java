package com.javaxiaobear.module.gc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireTemplateMapper;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireTemplateService;

/**
 * 问卷模板Service业务层处理
 * 
 * @author javaxiaobear
 */
@Service
public class GcQuestionnaireTemplateServiceImpl implements IGcQuestionnaireTemplateService {
    
    @Autowired
    private GcQuestionnaireTemplateMapper templateMapper;
    
    /**
     * 查询问卷模板列表
     * 
     * @param template 问卷模板
     * @return 问卷模板集合
     */
    @Override
    public List<GcQuestionnaireTemplate> selectTemplateList(GcQuestionnaireTemplate template) {
        return templateMapper.selectTemplateList(template);
    }
    
    /**
     * 查询问卷模板详情
     * 
     * @param templateId 模板ID
     * @return 问卷模板
     */
    @Override
    public GcQuestionnaireTemplate selectTemplateById(Long templateId) {
        return templateMapper.selectTemplateById(templateId);
    }
    
    /**
     * 查询启用的问卷模板列表（用于前端下拉选择）
     * 
     * @return 启用的问卷模板集合
     */
    @Override
    public List<GcQuestionnaireTemplate> selectActiveTemplates() {
        return templateMapper.selectActiveTemplates();
    }
    
    /**
     * 新增问卷模板
     * 
     * @param template 问卷模板
     * @return 结果
     */
    @Override
    public int insertTemplate(GcQuestionnaireTemplate template) {
        // 1. 验证模板编码唯一性
        if (!checkTemplateCodeUnique(template.getTemplateCode())) {
            throw new ServiceException("模板编码已存在");
        }
        
        // 2. 验证模板内容不为空
        if (template.getTemplateContent() == null || template.getTemplateContent().isEmpty()) {
            throw new ServiceException("问卷内容不能为空");
        }
        
        // 3. 验证重点人群阈值
        if (template.getFocusGroupThreshold() == null || template.getFocusGroupThreshold() < 0) {
            throw new ServiceException("重点人群阈值必须大于等于0");
        }
        
        // 4. 设置默认值
        if (template.getIsActive() == null) {
            template.setIsActive(0); // 默认未启用
        }
        
        if (template.getStatus() == null) {
            template.setStatus("0"); // 默认正常
        }
        
        return templateMapper.insertTemplate(template);
    }
    
    /**
     * 修改问卷模板
     * 
     * @param template 问卷模板
     * @return 结果
     */
    @Override
    public int updateTemplate(GcQuestionnaireTemplate template) {
        // 1. 验证模板是否存在
        GcQuestionnaireTemplate existTemplate = templateMapper.selectTemplateById(template.getTemplateId());
        if (existTemplate == null) {
            throw new ServiceException("问卷模板不存在");
        }
        
        // 2. 如果模板已被使用，不允许修改模板内容和阈值
        int useCount = templateMapper.checkTemplateInUse(template.getTemplateId());
        if (useCount > 0) {
            if (template.getTemplateContent() != null && 
                !template.getTemplateContent().equals(existTemplate.getTemplateContent())) {
                throw new ServiceException("模板已被使用，不允许修改问卷内容");
            }
            if (template.getFocusGroupThreshold() != null && 
                !template.getFocusGroupThreshold().equals(existTemplate.getFocusGroupThreshold())) {
                throw new ServiceException("模板已被使用，不允许修改重点人群阈值");
            }
        }
        
        // 3. 验证重点人群阈值
        if (template.getFocusGroupThreshold() != null && template.getFocusGroupThreshold() < 0) {
            throw new ServiceException("重点人群阈值必须大于等于0");
        }
        
        return templateMapper.updateTemplate(template);
    }
    
    /**
     * 删除问卷模板
     * 
     * @param templateId 模板ID
     * @return 结果
     */
    @Override
    public int deleteTemplateById(Long templateId) {
        // 检查模板是否已被使用
        int useCount = templateMapper.checkTemplateInUse(templateId);
        if (useCount > 0) {
            throw new ServiceException("该模板已被使用，不允许删除");
        }
        
        return templateMapper.deleteTemplateById(templateId);
    }
    
    /**
     * 批量删除问卷模板
     * 
     * @param templateIds 模板ID数组
     * @return 结果
     */
    @Override
    public int deleteTemplateByIds(Long[] templateIds) {
        // 检查每个模板是否已被使用
        for (Long templateId : templateIds) {
            int useCount = templateMapper.checkTemplateInUse(templateId);
            if (useCount > 0) {
                throw new ServiceException("模板ID:" + templateId + " 已被使用，不允许删除");
            }
        }
        
        return templateMapper.deleteTemplateByIds(templateIds);
    }
    
    /**
     * 校验模板编码唯一性
     * 
     * @param templateCode 模板编码
     * @return true唯一 false不唯一
     */
    @Override
    public boolean checkTemplateCodeUnique(String templateCode) {
        GcQuestionnaireTemplate template = templateMapper.selectTemplateByCode(templateCode);
        return template == null;
    }
}
