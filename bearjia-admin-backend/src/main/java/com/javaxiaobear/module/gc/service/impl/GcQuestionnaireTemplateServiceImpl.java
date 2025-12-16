package com.javaxiaobear.module.gc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireTemplateMapper;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireTemplateService;

/**
 * 问卷模板Service业务层处理
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Service
public class GcQuestionnaireTemplateServiceImpl implements IGcQuestionnaireTemplateService {
    
    @Autowired
    private GcQuestionnaireTemplateMapper templateMapper;

    @Override
    public List<GcQuestionnaireTemplate> selectTemplateList(GcQuestionnaireTemplate template) {
        return templateMapper.selectTemplateList(template);
    }

    @Override
    public GcQuestionnaireTemplate selectTemplateById(Long templateId) {
        return templateMapper.selectTemplateById(templateId);
    }

    @Override
    public List<GcQuestionnaireTemplate> selectEnabledTemplates() {
        return templateMapper.selectEnabledTemplates();
    }

    @Override
    public int insertTemplate(GcQuestionnaireTemplate template) {
        // 验证模板编码唯一性
        if (StringUtils.isNotEmpty(template.getTemplateCode())) {
            GcQuestionnaireTemplate existTemplate = templateMapper.selectTemplateByCode(template.getTemplateCode());
            if (existTemplate != null) {
                throw new RuntimeException("模板编码已存在");
            }
        }
        
        // 默认状态为草稿
        if (StringUtils.isEmpty(template.getStatus())) {
            template.setStatus("0");
        }
        
        // 默认重点人群阈值为60
        if (template.getFocusThreshold() == null) {
            template.setFocusThreshold(60);
        }
        
        return templateMapper.insertTemplate(template);
    }

    @Override
    public int updateTemplate(GcQuestionnaireTemplate template) {
        // 不允许修改模板编码
        template.setTemplateCode(null);
        
        return templateMapper.updateTemplate(template);
    }

    @Override
    public int deleteTemplateById(Long templateId) {
        return templateMapper.deleteTemplateById(templateId);
    }

    @Override
    public boolean checkTemplateCodeUnique(GcQuestionnaireTemplate template) {
        Long templateId = template.getTemplateId() == null ? -1L : template.getTemplateId();
        GcQuestionnaireTemplate existTemplate = templateMapper.selectTemplateByCode(template.getTemplateCode());
        if (existTemplate != null && !existTemplate.getTemplateId().equals(templateId)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkTemplateExistRecord(Long templateId) {
        int result = templateMapper.checkTemplateExistRecord(templateId);
        return result > 0;
    }
}
