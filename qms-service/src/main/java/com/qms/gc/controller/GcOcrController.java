package com.qms.gc.controller;

import com.qms.common.core.web.controller.BaseController;
import com.qms.common.core.web.domain.AjaxResult;
import com.qms.gc.domain.vo.OcrIdCardVO;
import com.qms.gc.service.IGcOcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * OCR身份证识别Controller
 * 
 * @author qms
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/gc/ocr")
public class GcOcrController extends BaseController {
    
    @Autowired
    private IGcOcrService ocrService;
    
    /**
     * 身份证OCR识别
     * 
     * @param file 身份证图片文件
     * @param side 身份证面：front-正面，back-反面
     * @return OCR识别结果
     */
    @PostMapping("/idcard")
    public AjaxResult ocrIdCard(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "side", defaultValue = "front") String side) {
        try {
            // 文件大小验证（最大5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return error("图片大小不能超过5MB");
            }
            
            // 文件格式验证
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return error("只支持图片格式文件");
            }
            
            // 调用OCR识别服务
            OcrIdCardVO result = ocrService.recognizeIdCard(file, side);
            
            return success(result);
        } catch (Exception e) {
            logger.error("OCR识别失败", e);
            return error("OCR识别失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量身份证OCR识别
     * 
     * @param files 身份证图片文件数组
     * @return OCR识别结果列表
     */
    @PostMapping("/idcard/batch")
    public AjaxResult batchOcrIdCard(@RequestParam("files") MultipartFile[] files) {
        try {
            // 批量数量限制
            if (files.length > 10) {
                return error("单次最多支持10张图片");
            }
            
            // 批量识别
            return success(ocrService.batchRecognizeIdCard(files));
        } catch (Exception e) {
            logger.error("批量OCR识别失败", e);
            return error("批量OCR识别失败：" + e.getMessage());
        }
    }
}
