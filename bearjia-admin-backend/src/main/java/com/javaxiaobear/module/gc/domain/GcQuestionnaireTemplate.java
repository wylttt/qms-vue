package com.javaxiaobear.module.gc.domain;

import com.javaxiaobear.common.core.domain.BaseEntity;
import org.apache.ibatis.type.Alias;

/**
 * 问卷模板对象 gc_questionnaire_template
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Alias("GcQuestionnaireTemplate")
public class GcQuestionnaireTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 模板名称 */
    private String templateName;

    /** 模板编码 */
    private String templateCode;

    /** 版本号 */
    private String version;

    /** 模板说明 */
    private String description;

    /** 问卷内容(JSON格式) */
    private String templateContent;

    /** 重点人群判定阈值 */
    private Integer focusThreshold;

    /** 状态(0草稿/1启用/2停用) */
    private String status;

    // Getter and Setter methods

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public Integer getFocusThreshold() {
        return focusThreshold;
    }

    public void setFocusThreshold(Integer focusThreshold) {
        this.focusThreshold = focusThreshold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GcQuestionnaireTemplate{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", focusThreshold=" + focusThreshold +
                ", status='" + status + '\'' +
                '}';
    }
}
