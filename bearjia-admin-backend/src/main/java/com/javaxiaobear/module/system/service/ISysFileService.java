package com.javaxiaobear.module.system.service;

import com.javaxiaobear.module.system.domain.SysFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 通用文件管理Service接口
 * 
 * @author javaxiaobear
 */
public interface ISysFileService {
    
    /**
     * 查询文件
     *
     * @param id 文件主键
     * @return 文件
     */
    public SysFile selectSysFileById(Long id);

    /**
     * 查询文件列表
     *
     * @param sysFile 文件
     * @return 文件集合
     */
    public List<SysFile> selectSysFileList(SysFile sysFile);

    /**
     * 根据业务类型和业务ID查询文件列表
     *
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件集合
     */
    public List<SysFile> selectFilesByBusiness(String businessType, Long businessId);

    /**
     * 查询RAG文档列表
     *
     * @param knowledgeBaseId 知识库ID
     * @return RAG文档集合
     */
    public List<SysFile> selectRagDocuments(Long knowledgeBaseId);

    /**
     * 查询用户头像
     *
     * @param userId 用户ID
     * @return 用户头像文件
     */
    public SysFile selectUserAvatar(Long userId);

    /**
     * 根据内容哈希查询文件（去重）
     *
     * @param contentHash 内容哈希
     * @return 文件
     */
    public SysFile selectFileByContentHash(String contentHash);

    /**
     * 统计文件数量
     *
     * @param sysFile 文件查询条件
     * @return 文件数量
     */
    public int countSysFiles(SysFile sysFile);

    /**
     * 统计业务类型文件数量
     *
     * @param businessType 业务类型
     * @return 文件数量
     */
    public int countFilesByBusinessType(String businessType);

    /**
     * 统计文件总大小
     *
     * @param businessType 业务类型
     * @return 文件总大小
     */
    public Long sumFileSizeByBusinessType(String businessType);

    /**
     * 新增文件
     *
     * @param sysFile 文件
     * @return 结果
     */
    public int insertSysFile(SysFile sysFile);

    /**
     * 修改文件
     *
     * @param sysFile 文件
     * @return 结果
     */
    public int updateSysFile(SysFile sysFile);

    /**
     * 更新文件状态
     *
     * @param id 文件ID
     * @param status 状态
     * @return 结果
     */
    public int updateFileStatus(Long id, Integer status);

    /**
     * 更新处理进度
     *
     * @param id 文件ID
     * @param progress 进度
     * @param errorMessage 错误信息
     * @return 结果
     */
    public int updateProcessProgress(Long id, Integer progress, String errorMessage);

    /**
     * 增加下载次数
     *
     * @param id 文件ID
     * @return 结果
     */
    public int incrementDownloadCount(Long id);

    /**
     * 批量删除文件
     *
     * @param ids 需要删除的文件主键集合
     * @return 结果
     */
    public int deleteSysFileByIds(Long[] ids);

    /**
     * 删除文件信息
     *
     * @param id 文件主键
     * @return 结果
     */
    public int deleteSysFileById(Long id);

    /**
     * 物理删除文件
     *
     * @param id 文件主键
     * @return 结果
     */
    public int deleteSysFilePhysically(Long id);

    /**
     * 批量物理删除文件
     *
     * @param ids 需要删除的文件主键集合
     * @return 结果
     */
    public int deleteSysFilePhysicallyByIds(Long[] ids);

    /**
     * 清理已删除的文件记录
     *
     * @param days 保留天数
     * @return 结果
     */
    public int cleanDeletedFiles(Integer days);

    /**
     * 查询重复文件
     *
     * @return 重复文件列表
     */
    public List<SysFile> selectDuplicateFiles();

    /**
     * 查询孤立文件
     *
     * @return 孤立文件列表
     */
    public List<SysFile> selectOrphanFiles();

    // 文件上传相关方法

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param parentId 父级文件夹ID
     * @return 文件信息
     */
    public SysFile uploadFile(MultipartFile file, String businessType, Long businessId, Long parentId);

    /**
     * 上传RAG文档
     *
     * @param file 上传的文件
     * @param knowledgeBaseId 知识库ID
     * @return 文件信息
     */
    public SysFile uploadRagDocument(MultipartFile file, Long knowledgeBaseId);

    /**
     * 上传用户头像
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件信息
     */
    public SysFile uploadUserAvatar(MultipartFile file, Long userId);

    /**
     * 上传系统附件
     *
     * @param file 上传的文件
     * @param relatedId 关联ID
     * @return 文件信息
     */
    public SysFile uploadSystemAttachment(MultipartFile file, Long relatedId);

    /**
     * 检查文件是否存在（基于内容哈希）
     *
     * @param contentHash 内容哈希
     * @return 是否存在
     */
    public boolean isFileExists(String contentHash);

    /**
     * 获取文件下载URL
     *
     * @param id 文件ID
     * @return 下载URL
     */
    public String getDownloadUrl(Long id);

    /**
     * 获取文件预览URL
     *
     * @param id 文件ID
     * @return 预览URL
     */
    public String getPreviewUrl(Long id);

    /**
     * 计算文件内容哈希值
     *
     * @param file 文件
     * @return 哈希值
     */
    public String calculateFileHash(MultipartFile file);

    /**
     * 查询文件夹树结构
     *
     * @param parentId 父级ID
     * @param businessType 业务类型
     * @return 文件夹列表
     */
    List<SysFile> selectFolderTree(Long parentId, String businessType);

    /**
     * 查询指定文件夹下的内容
     *
     * @param parentId 父级ID
     * @param businessType 业务类型
     * @return 文件和文件夹列表
     */
    List<SysFile> selectFilesByParentId(Long parentId, String businessType);

    /**
     * 创建文件夹
     *
     * @param folderName 文件夹名称
     * @param parentId 父级ID
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 创建的文件夹
     */
    SysFile createFolder(String folderName, Long parentId, String businessType, Long businessId);

    /**
     * 移动文件到指定文件夹
     *
     * @param fileId 文件ID
     * @param targetParentId 目标父级ID
     * @return 结果
     */
    int moveFileToFolder(Long fileId, Long targetParentId);

    /**
     * 批量移动文件
     *
     * @param fileIds 文件ID列表
     * @param targetParentId 目标父级ID
     * @return 结果
     */
    int batchMoveFiles(List<Long> fileIds, Long targetParentId);

    /**
     * 检查文件夹名称是否存在
     *
     * @param parentId 父级ID
     * @param folderName 文件夹名称
     * @param businessType 业务类型
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean checkFolderNameExists(Long parentId, String folderName, String businessType, Long excludeId);

    /**
     * 获取文件夹路径
     *
     * @param folderId 文件夹ID
     * @return 路径信息
     */
    List<SysFile> getFolderPath(Long folderId);

    /**
     * 重命名文件或文件夹
     *
     * @param fileId 文件ID
     * @param newName 新名称
     * @return 结果
     */
    int renameFile(Long fileId, String newName);
}
