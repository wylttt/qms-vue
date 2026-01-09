package com.javaxiaobear.module.gc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 身份证号验证工具类
 * 
 * @author javaxiaobear
 */
public class IdCardValidator {
    
    /**
     * 验证身份证号格式
     * 
     * @param idCardNo 身份证号
     * @return 是否合法
     */
    public static boolean validate(String idCardNo) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return false;
        }
        
        // 验证前17位是否为数字
        String num17 = idCardNo.substring(0, 17);
        if (!num17.matches("\\d{17}")) {
            return false;
        }
        
        // 验证校验码
        char checkCode = calculateCheckCode(num17);
        return checkCode == idCardNo.charAt(17);
    }
    
    /**
     * 从身份证号解析性别
     * 
     * @param idCardNo 身份证号
     * @return 性别（0男/1女）
     */
    public static String parseGender(String idCardNo) {
        if (!validate(idCardNo)) {
            return null;
        }
        int genderCode = Integer.parseInt(idCardNo.substring(16, 17));
        return genderCode % 2 == 0 ? "1" : "0"; // 0男1女
    }
    
    /**
     * 从身份证号解析出生日期
     * 
     * @param idCardNo 身份证号
     * @return 出生日期
     */
    public static Date parseBirthDate(String idCardNo) {
        if (!validate(idCardNo)) {
            return null;
        }
        String birthStr = idCardNo.substring(6, 14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(birthStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * 计算校验码
     * 
     * @param num17 前17位数字
     * @return 校验码
     */
    private static char calculateCheckCode(String num17) {
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (num17.charAt(i) - '0') * weight[i];
        }
        
        return checkCode[sum % 11];
    }
    
    /**
     * 验证身份证号与性别一致性
     * 
     * @param idCardNo 身份证号
     * @param gender 性别（0男/1女）
     * @return 是否一致
     */
    public static boolean validateGender(String idCardNo, String gender) {
        String parsedGender = parseGender(idCardNo);
        return parsedGender != null && parsedGender.equals(gender);
    }
    
    /**
     * 验证身份证号与出生日期一致性
     * 
     * @param idCardNo 身份证号
     * @param birthDate 出生日期
     * @return 是否一致
     */
    public static boolean validateBirthDate(String idCardNo, Date birthDate) {
        Date parsedBirthDate = parseBirthDate(idCardNo);
        if (parsedBirthDate == null || birthDate == null) {
            return false;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(parsedBirthDate).equals(sdf.format(birthDate));
    }
}
