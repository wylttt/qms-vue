package com.javaxiaobear.module.system.service.impl;

import com.javaxiaobear.base.common.constant.Constants;
import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.base.common.utils.DateUtils;
import com.javaxiaobear.base.common.utils.SecurityUtils;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.base.common.utils.file.FileUploadUtils;
import com.javaxiaobear.base.framework.config.ProjectConfig;
import com.javaxiaobear.module.system.domain.SysFile;
import com.javaxiaobear.module.system.mapper.SysFileMapper;
import com.javaxiaobear.module.system.service.ISysFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 通用文件管理Service业务层处理
 * 
 * @author javaxiaobear
 */
@Service
public class SysFileServiceImpl implements ISysFileService {
    
    private static final Logger log = LoggerFactory.getLogger(SysFileServiceImpl.class);

    @Autowired
    private SysFileMapper sysFileMapper;

    /**
     * 查询文件
     *
     * @param id 文件主键
     * @return 文件
     */
    @Override
    public SysFile selectSysFileById(Long id) {
        return sysFileMapper.selectSysFileById(id);
    }

    /**
     * 查询文件列表
     *
     * @param sysFile 文件
     * @return 文件
     */
    @Override
    public List<SysFile> selectSysFileList(SysFile sysFile) {
        return sysFileMapper.selectSysFileList(sysFile);
    }

    /**
     * 根据业务类型和业务ID查询文件列表
     */
    @Override
    public List<SysFile> selectFilesByBusiness(String businessType, Long businessId) {
        return sysFileMapper.selectFilesByBusiness(businessType, businessId);
    }

    /**
     * 查询RAG文档列表
     */
    @Override
    public List<SysFile> selectRagDocuments(Long knowledgeBaseId) {
        return sysFileMapper.selectRagDocuments(knowledgeBaseId);
    }

    /**
     * 查询用户头像
     */
    @Override
    public SysFile selectUserAvatar(Long userId) {
        return sysFileMapper.selectUserAvatar(userId);
    }

    /**
     * 根据内容哈希查询文件
     */
    @Override
    public SysFile selectFileByContentHash(String contentHash) {
        return sysFileMapper.selectFileByContentHash(contentHash);
    }

    /**
     * 统计文件数量
     */
    @Override
    public int countSysFiles(SysFile sysFile) {
        return sysFileMapper.countSysFiles(sysFile);
    }

    /**
     * 统计业务类型文件数量
     */
    @Override
    public int countFilesByBusinessType(String businessType) {
        return sysFileMapper.countFilesByBusinessType(businessType);
    }

    /**
     * 统计文件总大小
     */
    @Override
    public Long sumFileSizeByBusinessType(String businessType) {
        return sysFileMapper.sumFileSizeByBusinessType(businessType);
    }

    /**
     * 新增文件
     *
     * @param sysFile 文件
     * @return 结果
     */
    @Override
    public int insertSysFile(SysFile sysFile) {
        sysFile.setCreateTime(DateUtils.getNowDate());
        sysFile.setCreateBy(SecurityUtils.getUsername());
        return sysFileMapper.insertSysFile(sysFile);
    }

    /**
     * 修改文件
     *
     * @param sysFile 文件
     * @return 结果
     */
    @Override
    public int updateSysFile(SysFile sysFile) {
        sysFile.setUpdateTime(DateUtils.getNowDate());
        sysFile.setUpdateBy(SecurityUtils.getUsername());
        return sysFileMapper.updateSysFile(sysFile);
    }

    /**
     * 更新文件状态
     */
    @Override
    public int updateFileStatus(Long id, Integer status) {
        return sysFileMapper.updateFileStatus(id, status);
    }

    /**
     * 更新处理进度
     */
    @Override
    public int updateProcessProgress(Long id, Integer progress, String errorMessage) {
        return sysFileMapper.updateProcessProgress(id, progress, errorMessage);
    }

    /**
     * 增加下载次数
     */
    @Override
    public int incrementDownloadCount(Long id) {
        return sysFileMapper.incrementDownloadCount(id);
    }

    /**
     * 批量删除文件
     *
     * @param ids 需要删除的文件主键
     * @return 结果
     */
    @Override
    public int deleteSysFileByIds(Long[] ids) {
        return sysFileMapper.deleteSysFileByIds(ids);
    }

    /**
     * 删除文件信息
     *
     * @param id 文件主键
     * @return 结果
     */
    @Override
    public int deleteSysFileById(Long id) {
        return sysFileMapper.deleteSysFileById(id);
    }

    /**
     * 物理删除文件
     */
    @Override
    public int deleteSysFilePhysically(Long id) {
        return sysFileMapper.deleteSysFilePhysically(id);
    }

    /**
     * 批量物理删除文件
     */
    @Override
    public int deleteSysFilePhysicallyByIds(Long[] ids) {
        return sysFileMapper.deleteSysFilePhysicallyByIds(ids);
    }

    /**
     * 清理已删除的文件记录
     */
    @Override
    public int cleanDeletedFiles(Integer days) {
        return sysFileMapper.cleanDeletedFiles(days);
    }

    /**
     * 查询重复文件
     */
    @Override
    public List<SysFile> selectDuplicateFiles() {
        return sysFileMapper.selectDuplicateFiles();
    }

    /**
     * 查询孤立文件
     */
    @Override
    public List<SysFile> selectOrphanFiles() {
        return sysFileMapper.selectOrphanFiles();
    }

    /**
     * 上传文件
     */
    @Override
    public SysFile uploadFile(MultipartFile file, String businessType, Long businessId, Long parentId) {
        try {
            // 计算文件哈希值
            String contentHash = calculateFileHash(file);
            
            // 检查文件是否已存在
            SysFile existingFile = selectFileByContentHash(contentHash);
            if (existingFile != null) {
                log.info("文件已存在，返回现有文件信息: {}", existingFile.getFileName());
                return existingFile;
            }

            // 上传文件
            String fileName = FileUploadUtils.upload(ProjectConfig.getProfile(), file);
            String fileUrl = fileName; // fileName已经包含了RESOURCE_PREFIX

            // 创建文件记录
            SysFile sysFile = new SysFile();
            sysFile.setParentId(parentId != null ? parentId : 0L);
            sysFile.setFileName(file.getOriginalFilename());
            sysFile.setOriginalName(file.getOriginalFilename());
            sysFile.setFileType(getFileExtension(file.getOriginalFilename()));
            sysFile.setFileSize(file.getSize());

            // 构建实际文件路径（用于文件系统访问）
            // fileName格式：/profile/2025/09/28/xxx.docx
            // 需要转换为：../upload/2025/09/28/xxx.docx（相对于bearjia-admin目录）
            String actualFileName = fileName.substring(Constants.RESOURCE_PREFIX.length()); // 移除/profile前缀
            String filePath = "../upload" + actualFileName; // 使用相对于bearjia-admin的路径
            sysFile.setFilePath(filePath);
            sysFile.setFileUrl(fileUrl);
            sysFile.setContentHash(contentHash);
            sysFile.setMimeType(file.getContentType());
            sysFile.setBusinessType(businessType != null ? businessType : "common");
            sysFile.setBusinessId(businessId);
            sysFile.setStatus("1");
            sysFile.setProcessProgress(100);
            sysFile.setDownloadCount(0);

            insertSysFile(sysFile);
            return sysFile;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传RAG文档
     */
    @Override
    public SysFile uploadRagDocument(MultipartFile file, Long knowledgeBaseId) {
        return uploadFile(file, "rag", knowledgeBaseId, 0L);
    }

    /**
     * 上传用户头像
     */
    @Override
    public SysFile uploadUserAvatar(MultipartFile file, Long userId) {
        return uploadFile(file, "avatar", userId, 0L);
    }

    /**
     * 上传系统附件
     */
    @Override
    public SysFile uploadSystemAttachment(MultipartFile file, Long relatedId) {
        return uploadFile(file, "attachment", relatedId, 0L);
    }

    /**
     * 检查文件是否存在
     */
    @Override
    public boolean isFileExists(String contentHash) {
        return selectFileByContentHash(contentHash) != null;
    }

    /**
     * 获取文件下载URL
     */
    @Override
    public String getDownloadUrl(Long id) {
        SysFile file = selectSysFileById(id);
        if (file != null) {
            return file.getFileUrl();
        }
        return null;
    }

    /**
     * 获取文件预览URL
     */
    @Override
    public String getPreviewUrl(Long id) {
        SysFile file = selectSysFileById(id);
        if (file != null) {
            return file.getFileUrl();
        }
        return null;
    }

    /**
     * 计算文件内容哈希值
     */
    @Override
    public String calculateFileHash(MultipartFile file) {
        try {
            return DigestUtils.md5Hex(file.getInputStream());
        } catch (IOException e) {
            log.error("计算文件哈希值失败", e);
            throw new ServiceException("计算文件哈希值失败");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 查询文件夹树结构
     */
    @Override
    public List<SysFile> selectFolderTree(Long parentId, String businessType) {
        return sysFileMapper.selectFolderTree(parentId, businessType);
    }

    /**
     * 查询指定文件夹下的内容
     */
    @Override
    public List<SysFile> selectFilesByParentId(Long parentId, String businessType) {
        return sysFileMapper.selectFilesByParentId(parentId, businessType);
    }

    /**
     * 创建文件夹
     */
    @Override
    public SysFile createFolder(String folderName, Long parentId, String businessType, Long businessId) {
        // 检查文件夹名称是否已存在
        if (checkFolderNameExists(parentId, folderName, businessType, null)) {
            throw new RuntimeException("文件夹名称已存在");
        }

        SysFile folder = new SysFile();
        folder.setParentId(parentId);
        folder.setIsFolder(1);
        folder.setFileName(folderName);
        folder.setOriginalName(folderName);
        folder.setBusinessType(businessType);
        folder.setBusinessId(businessId);
        folder.setStatus("1");
        folder.setCreateTime(DateUtils.getNowDate());

        try {
            folder.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // 忽略获取用户信息失败的情况
        }

        sysFileMapper.createFolder(folder);
        return folder;
    }

    /**
     * 移动文件到指定文件夹
     */
    @Override
    public int moveFileToFolder(Long fileId, Long targetParentId) {
        return sysFileMapper.moveFileToFolder(fileId, targetParentId);
    }

    /**
     * 批量移动文件
     */
    @Override
    public int batchMoveFiles(List<Long> fileIds, Long targetParentId) {
        return sysFileMapper.batchMoveFiles(fileIds, targetParentId);
    }

    /**
     * 检查文件夹名称是否存在
     */
    @Override
    public boolean checkFolderNameExists(Long parentId, String folderName, String businessType, Long excludeId) {
        return sysFileMapper.checkFolderNameExists(parentId, folderName, businessType, excludeId);
    }

    /**
     * 获取文件夹路径
     */
    @Override
    public List<SysFile> getFolderPath(Long folderId) {
        return sysFileMapper.getFolderPath(folderId);
    }

    /**
     * 重命名文件或文件夹
     */
    @Override
    public int renameFile(Long fileId, String newName) {
        SysFile file = new SysFile();
        file.setId(fileId);
        file.setFileName(newName);
        file.setOriginalName(newName);
        file.setUpdateTime(DateUtils.getNowDate());

        try {
            file.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            // 忽略获取用户信息失败的情况
        }

        return sysFileMapper.updateSysFile(file);
    }
}
