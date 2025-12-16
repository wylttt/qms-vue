package com.qms.gc.service;

import com.qms.gc.domain.vo.OcrIdCardVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OCR识别Service接口
 * 
 * @author qms
 * @date 2025-12-16
 */
public interface IGcOcrService {
    
    /**
     * 身份证OCR识别
     * 
     * @param file 身份证图片文件
     * @param side 身份证面：front-正面，back-反面
     * @return OCR识别结果
     */
    OcrIdCardVO recognizeIdCard(MultipartFile file, String side) throws Exception;
    
    /**
     * 批量身份证OCR识别
     * 
     * @param files 身份证图片文件数组
     * @return OCR识别结果列表
     */
    List<OcrIdCardVO> batchRecognizeIdCard(MultipartFile[] files) throws Exception;
}
