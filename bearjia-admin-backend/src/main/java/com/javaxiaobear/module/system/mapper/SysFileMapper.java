package com.javaxiaobear.module.system.mapper;

import com.javaxiaobear.module.system.domain.SysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用文件管理Mapper接口
 * 
 * @author javaxiaobear
 */
public interface SysFileMapper {
    
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
    public List<SysFile> selectFilesByBusiness(@Param("businessType") String businessType, @Param("businessId") Long businessId);

    /**
     * 根据内容哈希查询文件（去重）
     *
     * @param contentHash 内容哈希
     * @return 文件
     */
    public SysFile selectFileByContentHash(@Param("contentHash") String contentHash);

    /**
     * 查询RAG文档列表
     *
     * @param knowledgeBaseId 知识库ID
     * @return RAG文档集合
     */
    public List<SysFile> selectRagDocuments(@Param("knowledgeBaseId") Long knowledgeBaseId);

    /**
     * 查询用户头像
     *
     * @param userId 用户ID
     * @return 用户头像文件
     */
    public SysFile selectUserAvatar(@Param("userId") Long userId);

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
    public int countFilesByBusinessType(@Param("businessType") String businessType);

    /**
     * 统计文件总大小
     *
     * @param businessType 业务类型
     * @return 文件总大小
     */
    public Long sumFileSizeByBusinessType(@Param("businessType") String businessType);

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
    public int updateFileStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新处理进度
     *
     * @param id 文件ID
     * @param progress 进度
     * @param errorMessage 错误信息
     * @return 结果
     */
    public int updateProcessProgress(@Param("id") Long id, @Param("progress") Integer progress, @Param("errorMessage") String errorMessage);

    /**
     * 增加下载次数
     *
     * @param id 文件ID
     * @return 结果
     */
    public int incrementDownloadCount(@Param("id") Long id);

    /**
     * 删除文件
     *
     * @param id 文件主键
     * @return 结果
     */
    public int deleteSysFileById(Long id);

    /**
     * 批量删除文件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFileByIds(Long[] ids);

    /**
     * 物理删除文件（真正删除记录）
     *
     * @param id 文件主键
     * @return 结果
     */
    public int deleteSysFilePhysically(Long id);

    /**
     * 批量物理删除文件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFilePhysicallyByIds(Long[] ids);

    /**
     * 清理已删除的文件记录
     *
     * @param days 保留天数
     * @return 结果
     */
    public int cleanDeletedFiles(@Param("days") Integer days);

    /**
     * 查询重复文件
     *
     * @return 重复文件列表
     */
    public List<SysFile> selectDuplicateFiles();

    /**
     * 查询孤立文件（没有业务关联的文件）
     *
     * @return 孤立文件列表
     */
    public List<SysFile> selectOrphanFiles();

    /**
     * 查询文件夹树结构
     *
     * @param parentId 父级ID
     * @param businessType 业务类型
     * @return 文件夹列表
     */
    public List<SysFile> selectFolderTree(@Param("parentId") Long parentId, @Param("businessType") String businessType);

    /**
     * 查询指定文件夹下的内容
     *
     * @param parentId 父级ID
     * @param businessType 业务类型
     * @return 文件和文件夹列表
     */
    public List<SysFile> selectFilesByParentId(@Param("parentId") Long parentId, @Param("businessType") String businessType);

    /**
     * 创建文件夹
     *
     * @param sysFile 文件夹信息
     * @return 结果
     */
    public int createFolder(SysFile sysFile);

    /**
     * 移动文件到指定文件夹
     *
     * @param fileId 文件ID
     * @param targetParentId 目标父级ID
     * @return 结果
     */
    public int moveFileToFolder(@Param("fileId") Long fileId, @Param("targetParentId") Long targetParentId);

    /**
     * 批量移动文件
     *
     * @param fileIds 文件ID列表
     * @param targetParentId 目标父级ID
     * @return 结果
     */
    public int batchMoveFiles(@Param("fileIds") List<Long> fileIds, @Param("targetParentId") Long targetParentId);

    /**
     * 检查文件夹名称是否存在
     *
     * @param parentId 父级ID
     * @param fileName 文件夹名称
     * @param businessType 业务类型
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    public boolean checkFolderNameExists(@Param("parentId") Long parentId,
                                        @Param("fileName") String fileName,
                                        @Param("businessType") String businessType,
                                        @Param("excludeId") Long excludeId);

    /**
     * 获取文件夹路径
     *
     * @param folderId 文件夹ID
     * @return 路径信息
     */
    public List<SysFile> getFolderPath(Long folderId);
}
