package com.qms.gc.domain.vo;

import lombok.Data;

/**
 * OCR身份证识别结果VO
 * 
 * @author qms
 * @date 2025-12-16
 */
@Data
public class OcrIdCardVO {
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 性别：0-男，1-女
     */
    private String gender;
    
    /**
     * 民族
     */
    private String nation;
    
    /**
     * 出生日期（YYYY-MM-DD）
     */
    private String birthDate;
    
    /**
     * 住址
     */
    private String address;
    
    /**
     * 签发机关（背面）
     */
    private String authority;
    
    /**
     * 有效期起始日期（背面）
     */
    private String validFrom;
    
    /**
     * 有效期结束日期（背面）
     */
    private String validTo;
    
    /**
     * 识别置信度（0-100）
     */
    private Integer confidence;
    
    /**
     * 识别方式：baidu-百度OCR，tencent-腾讯OCR，aliyun-阿里云OCR
     */
    private String provider;
    
    /**
     * 识别耗时（毫秒）
     */
    private Long duration;
}
