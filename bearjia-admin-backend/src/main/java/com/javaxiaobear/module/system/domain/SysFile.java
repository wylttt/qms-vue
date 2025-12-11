package com.javaxiaobear.module.system.domain;


import com.javaxiaobear.base.framework.web.domain.BaseEntity;

/**
 * 文件信息表 sys_file
 * 
 * @author javaxiaobear
 */
public class SysFile extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 文件id */
    private Long id;
    
    /** 父级ID */
    private Long parentId;
    
    /** 是否为文件夹 */
    private Integer isFolder;

    /** 文件名称 */
    private String fileName;
    
    /** 原始文件名 */
    private String originalName;

    /** 文件路径 */
    private String filePath;
    
    /** 文件URL */
    private String fileUrl;

    /** 文件大小 */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 文件扩展名 */
    private String fileExt;
    
    /** 内容哈希值 */
    private String contentHash;
    
    /** MIME类型 */
    private String mimeType;
    
    /** 业务类型 */
    private String businessType;
    
    /** 业务ID */
    private Long businessId;

    /** 文件状态 */
    private String status;
    
    /** 处理进度 */
    private Integer processProgress;
    
    /** 下载次数 */
    private Integer downloadCount;
    
    /** 错误信息 */
    private String errorMessage;

    private String metadata;

    public SysFile() {
    }

    public SysFile(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Integer isFolder) {
        this.isFolder = isFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProcessProgress() {
        return processProgress;
    }

    public void setProcessProgress(Integer processProgress) {
        this.processProgress = processProgress;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SysFile{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", isFolder=" + isFolder +
                ", fileName='" + fileName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", contentHash='" + contentHash + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", businessType='" + businessType + '\'' +
                ", businessId=" + businessId +
                ", status='" + status + '\'' +
                ", processProgress=" + processProgress +
                ", downloadCount=" + downloadCount +
                ", errorMessage='" + errorMessage + '\'' +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
