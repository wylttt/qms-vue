package com.javaxiaobear.module.gc.domain.entity;

import com.javaxiaobear.base.framework.web.domain.BaseEntity;

/**
 * 问卷模板实体类
 * 
 * @author javaxiaobear
 */
public class GcQuestionnaireTemplate extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 模板ID（主键） */
    private Long templateId;

    /** 模板名称 */
    private String templateName;

    /** 模板编码（唯一） */
    private String templateCode;

    /** 版本号 */
    private String version;

    /** 描述 */
    private String description;

    /** 问卷内容（JSON字符串） */
    private String templateContent;

    /** 重点人群阈值 */
    private Integer focusGroupThreshold;

    /** 是否启用（0否/1是） */
    private Integer isActive;

    /** 状态（0正常/1停用） */
    private String status;

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

    public Integer getFocusGroupThreshold() {
        return focusGroupThreshold;
    }

    public void setFocusGroupThreshold(Integer focusGroupThreshold) {
        this.focusGroupThreshold = focusGroupThreshold;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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
                ", focusGroupThreshold=" + focusGroupThreshold +
                ", isActive=" + isActive +
                ", status='" + status + '\'' +
                '}';
    }
}
