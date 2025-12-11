package com.javaxiaobear.module.system.controller;

import com.javaxiaobear.base.framework.aspectj.lang.annotation.Log;
import com.javaxiaobear.base.framework.aspectj.lang.enums.BusinessType;
import com.javaxiaobear.base.framework.web.controller.BaseController;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.base.framework.web.page.TableDataInfo;
import com.javaxiaobear.module.system.domain.SysFile;
import com.javaxiaobear.module.system.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 通用文件管理Controller
 * 
 * @author javaxiaobear
 */
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {
    
    @Autowired
    private ISysFileService sysFileService;

    /**
     * 查询文件列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFile sysFile) {
        startPage();
        List<SysFile> list = sysFileService.selectSysFileList(sysFile);
        return getDataTable(list);
    }

    /**
     * 获取文件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(sysFileService.selectSysFileById(id));
    }

    /**
     * 根据业务类型查询文件列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/business/{businessType}")
    public AjaxResult getFilesByBusiness(@PathVariable("businessType") String businessType, 
                                       @RequestParam(value = "businessId", required = false) Long businessId) {
        List<SysFile> files = sysFileService.selectFilesByBusiness(businessType, businessId);
        return success(files);
    }

    /**
     * 查询RAG文档列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/rag")
    public AjaxResult getRagDocuments(@RequestParam(value = "knowledgeBaseId", required = false) Long knowledgeBaseId) {
        List<SysFile> files = sysFileService.selectRagDocuments(knowledgeBaseId);
        return success(files);
    }

    /**
     * 查询用户头像
     */
    @GetMapping("/avatar/{userId}")
    public AjaxResult getUserAvatar(@PathVariable("userId") Long userId) {
        SysFile avatar = sysFileService.selectUserAvatar(userId);
        return success(avatar);
    }

    /**
     * 统计文件信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/stats")
    public AjaxResult getFileStats() {
        AjaxResult result = AjaxResult.success();
        
        // 统计各业务类型文件数量
        result.put("ragCount", sysFileService.countFilesByBusinessType("rag"));
        result.put("avatarCount", sysFileService.countFilesByBusinessType("avatar"));
        result.put("attachmentCount", sysFileService.countFilesByBusinessType("attachment"));
        result.put("commonCount", sysFileService.countFilesByBusinessType("common"));
        
        // 统计各业务类型文件大小
        result.put("ragSize", sysFileService.sumFileSizeByBusinessType("rag"));
        result.put("avatarSize", sysFileService.sumFileSizeByBusinessType("avatar"));
        result.put("attachmentSize", sysFileService.sumFileSizeByBusinessType("attachment"));
        result.put("commonSize", sysFileService.sumFileSizeByBusinessType("common"));
        
        return result;
    }

    /**
     * 查询重复文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/duplicates")
    public AjaxResult getDuplicateFiles() {
        List<SysFile> files = sysFileService.selectDuplicateFiles();
        return success(files);
    }

    /**
     * 查询孤立文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/orphans")
    public AjaxResult getOrphanFiles() {
        List<SysFile> files = sysFileService.selectOrphanFiles();
        return success(files);
    }

    /**
     * 上传文件
     */
    @Log(title = "文件上传", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "businessType", required = false, defaultValue = "common") String businessType,
                               @RequestParam(value = "businessId", required = false) Long businessId,
                               @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId) {
        try {
            SysFile sysFile = sysFileService.uploadFile(file, businessType, businessId, parentId);
            // 返回标准的成功响应格式
            AjaxResult result = AjaxResult.success("上传成功");
            result.put("fileName", sysFile.getFileUrl());
            result.put("url", sysFile.getFileUrl());
            result.put("data", sysFile);
            return result;
        } catch (Exception e) {
            return error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传RAG文档
     */
    @Log(title = "RAG文档上传", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('rag:document:upload')")
    @PostMapping("/upload/rag")
    public AjaxResult uploadRagDocument(@RequestParam("file") MultipartFile file,
                                      @RequestParam("knowledgeBaseId") Long knowledgeBaseId) {
        try {
            SysFile sysFile = sysFileService.uploadRagDocument(file, knowledgeBaseId);
            return success(sysFile);
        } catch (Exception e) {
            return error("RAG文档上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传用户头像
     */
    @Log(title = "用户头像上传", businessType = BusinessType.INSERT)
    @PostMapping("/upload/avatar")
    public AjaxResult uploadUserAvatar(@RequestParam("file") MultipartFile file,
                                     @RequestParam("userId") Long userId) {
        try {
            SysFile sysFile = sysFileService.uploadUserAvatar(file, userId);
            return success(sysFile);
        } catch (Exception e) {
            return error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传系统附件
     */
    @Log(title = "系统附件上传", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @PostMapping("/upload/attachment")
    public AjaxResult uploadSystemAttachment(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "relatedId", required = false) Long relatedId) {
        try {
            SysFile sysFile = sysFileService.uploadSystemAttachment(file, relatedId);
            return success(sysFile);
        } catch (Exception e) {
            return error("附件上传失败: " + e.getMessage());
        }
    }

    /**
     * 修改文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "文件管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysFile sysFile) {
        return toAjax(sysFileService.updateSysFile(sysFile));
    }

    /**
     * 更新文件状态
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "文件状态更新", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{id}/{status}")
    public AjaxResult updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        return toAjax(sysFileService.updateFileStatus(id, status));
    }

    /**
     * 更新处理进度
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "文件处理进度更新", businessType = BusinessType.UPDATE)
    @PutMapping("/progress/{id}")
    public AjaxResult updateProgress(@PathVariable("id") Long id, 
                                   @RequestParam("progress") Integer progress,
                                   @RequestParam(value = "errorMessage", required = false) String errorMessage) {
        return toAjax(sysFileService.updateProcessProgress(id, progress, errorMessage));
    }

    /**
     * 删除文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sysFileService.deleteSysFileByIds(ids));
    }

    /**
     * 物理删除文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "文件物理删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/physical/{ids}")
    public AjaxResult removePhysically(@PathVariable Long[] ids) {
        return toAjax(sysFileService.deleteSysFilePhysicallyByIds(ids));
    }

    /**
     * 清理已删除的文件记录
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "清理已删除文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean/{days}")
    public AjaxResult cleanDeletedFiles(@PathVariable("days") Integer days) {
        int count = sysFileService.cleanDeletedFiles(days);
        return success("成功清理 " + count + " 个已删除的文件记录");
    }

    /**
     * 获取文件下载URL
     */
    @GetMapping("/download/{id}")
    public AjaxResult getDownloadUrl(@PathVariable("id") Long id) {
        String url = sysFileService.getDownloadUrl(id);
        if (url != null) {
            // 增加下载次数
            sysFileService.incrementDownloadCount(id);
            return AjaxResult.success("url", url);
        }
        return error("文件不存在");
    }

    /**
     * 获取文件预览URL
     */
    @GetMapping("/preview/{id}")
    public AjaxResult getPreviewUrl(@PathVariable("id") Long id) {
        String url = sysFileService.getPreviewUrl(id);
        if (url != null) {
            return AjaxResult.success("url", url);
        }
        return error("文件不存在");
    }

    /**
     * 检查文件是否存在
     */
    @PostMapping("/exists")
    public AjaxResult checkFileExists(@RequestParam("contentHash") String contentHash) {
        boolean exists = sysFileService.isFileExists(contentHash);
        return AjaxResult.success("exists", exists);
    }

    /**
     * 查询文件夹树结构
     */
    @GetMapping("/folderTree")
    public AjaxResult getFolderTree(@RequestParam(required = false) Long parentId,
                                   @RequestParam(required = false) String businessType) {
        List<SysFile> folders = sysFileService.selectFolderTree(parentId, businessType);
        return success(folders);
    }

    /**
     * 查询指定文件夹下的内容
     */
    @GetMapping("/folder/{parentId}")
    public AjaxResult getFilesByParentId(@PathVariable Long parentId,
                                        @RequestParam(required = false) String businessType) {
        List<SysFile> files = sysFileService.selectFilesByParentId(parentId, businessType);
        return success(files);
    }

    /**
     * 创建文件夹
     */
    @PreAuthorize("@ss.hasPermi('system:file:add')")
    @Log(title = "创建文件夹", businessType = BusinessType.INSERT)
    @PostMapping("/createFolder")
    public AjaxResult createFolder(@RequestParam String folderName,
                                  @RequestParam(defaultValue = "0") Long parentId,
                                  @RequestParam(required = false) String businessType,
                                  @RequestParam(required = false) Long businessId) {
        try {
            SysFile folder = sysFileService.createFolder(folderName, parentId, businessType, businessId);
            return success(folder);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 移动文件到指定文件夹
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "移动文件", businessType = BusinessType.UPDATE)
    @PostMapping("/move")
    public AjaxResult moveFiles(@RequestParam List<Long> fileIds,
                               @RequestParam Long targetParentId) {
        try {
            int result = sysFileService.batchMoveFiles(fileIds, targetParentId);
            return toAjax(result);
        } catch (Exception e) {
            return error("移动文件失败: " + e.getMessage());
        }
    }

    /**
     * 重命名文件或文件夹
     */
    @PreAuthorize("@ss.hasPermi('system:file:edit')")
    @Log(title = "重命名文件", businessType = BusinessType.UPDATE)
    @PostMapping("/rename")
    public AjaxResult renameFile(@RequestParam Long fileId,
                                @RequestParam String newName) {
        try {
            int result = sysFileService.renameFile(fileId, newName);
            return toAjax(result);
        } catch (Exception e) {
            return error("重命名失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件夹路径
     */
    @GetMapping("/path/{folderId}")
    public AjaxResult getFolderPath(@PathVariable Long folderId) {
        List<SysFile> path = sysFileService.getFolderPath(folderId);
        return success(path);
    }

    /**
     * 检查文件夹名称是否存在
     */
    @GetMapping("/checkFolderName")
    public AjaxResult checkFolderName(@RequestParam Long parentId,
                                     @RequestParam String folderName,
                                     @RequestParam(required = false) String businessType,
                                     @RequestParam(required = false) Long excludeId) {
        boolean exists = sysFileService.checkFolderNameExists(parentId, folderName, businessType, excludeId);
        return success(!exists); // 返回是否可用
    }
}
