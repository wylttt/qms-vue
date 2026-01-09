package com.javaxiaobear.module.gc.util;

/**
 * 身份证脱敏工具类
 * 
 * @author javaxiaobear
 */
public class IdCardDesensitizer {
    
    /**
     * 根据角色脱敏身份证号
     * 
     * @param idCardNo 原始身份证号
     * @param roleKey 用户角色标识
     * @return 脱敏后的身份证号
     */
    public static String desensitize(String idCardNo, String roleKey) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return idCardNo;
        }
        
        if (roleKey == null) {
            return partialDesensitize(idCardNo);
        }
        
        // 医院/医生不返回
        if (roleKey.contains("hospital") || roleKey.contains("doctor")) {
            return null;
        }
        
        // 采血点管理员：完全脱敏（仅保留首尾）
        if (roleKey.contains("sampling_site")) {
            return fullDesensitize(idCardNo);
        }
        
        // 其他角色：部分脱敏（保留前3位和后4位）
        return partialDesensitize(idCardNo);
    }
    
    /**
     * 完全脱敏（仅保留首尾）
     * 示例：3***************6
     */
    private static String fullDesensitize(String idCardNo) {
        return idCardNo.substring(0, 1) + "***************" + idCardNo.substring(17);
    }
    
    /**
     * 部分脱敏（保留前3位和后4位）
     * 示例：360***********1234
     */
    private static String partialDesensitize(String idCardNo) {
        return idCardNo.substring(0, 3) + "***********" + idCardNo.substring(14);
    }
}
