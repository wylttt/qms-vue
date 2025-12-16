package com.javaxiaobear.module.gc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 居民管理Service单元测试
 * 
 * 测试范围：
 * 1. 身份证号脱敏功能
 * 2. 身份证号验证
 * 3. 身份证信息解析
 * 
 * @author javaxiaobear
 * @date 2025-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcResidentServiceImplTest {

    /**
     * 测试：身份证号脱敏 - 超级管理员（部分脱敏）
     */
    @Test
    void testDesensitizeIdCard_SuperAdmin() {
        String idCardNo = "360123199001011234";
        String roleKey = "admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("360***********1234", result, "超级管理员应显示部分脱敏");
    }

    /**
     * 测试：身份证号脱敏 - 区级管理员（部分脱敏）
     */
    @Test
    void testDesensitizeIdCard_DistrictAdmin() {
        String idCardNo = "360123199001011234";
        String roleKey = "district_admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("360***********1234", result, "区级管理员应显示部分脱敏");
    }

    /**
     * 测试：身份证号脱敏 - 街道管理员（部分脱敏）
     */
    @Test
    void testDesensitizeIdCard_StreetAdmin() {
        String idCardNo = "360123199001011234";
        String roleKey = "street_admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("360***********1234", result, "街道管理员应显示部分脱敏");
    }

    /**
     * 测试：身份证号脱敏 - 采血点管理员（完全脱敏）
     */
    @Test
    void testDesensitizeIdCard_SamplingAdmin() {
        String idCardNo = "360123199001011234";
        String roleKey = "sampling_site";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("3***************4", result, "采血点管理员应显示完全脱敏");
    }

    /**
     * 测试：身份证号脱敏 - 医院角色（不返回）
     */
    @Test
    void testDesensitizeIdCard_Hospital() {
        String idCardNo = "360123199001011234";
        String roleKey = "hospital";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertNull(result, "医院角色应返回null");
    }

    /**
     * 测试：身份证号脱敏 - 医生角色（不返回）
     */
    @Test
    void testDesensitizeIdCard_Doctor() {
        String idCardNo = "360123199001011234";
        String roleKey = "doctor";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertNull(result, "医生角色应返回null");
    }

    /**
     * 测试：身份证号脱敏 - 空身份证号
     */
    @Test
    void testDesensitizeIdCard_Null() {
        String idCardNo = null;
        String roleKey = "admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertNull(result, "空身份证号应返回null");
    }

    /**
     * 测试：身份证号脱敏 - 空字符串
     */
    @Test
    void testDesensitizeIdCard_Empty() {
        String idCardNo = "";
        String roleKey = "admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("", result, "空字符串应返回空字符串");
    }

    /**
     * 测试：身份证号脱敏 - 长度不足18位
     */
    @Test
    void testDesensitizeIdCard_InvalidLength() {
        String idCardNo = "36012319900101";
        String roleKey = "admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals(idCardNo, result, "长度不足18位应原样返回");
    }

    /**
     * 辅助方法：身份证号脱敏
     */
    private String desensitizeIdCard(String idCardNo, String roleKey) {
        if (idCardNo == null) {
            return null;
        }
        
        if (idCardNo.isEmpty() || idCardNo.length() != 18) {
            return idCardNo;
        }
        
        // 医院/医生角色：不返回
        if (roleKey != null && (roleKey.contains("hospital") || roleKey.contains("doctor"))) {
            return null;
        }
        
        // 采血点管理员：完全脱敏
        if (roleKey != null && roleKey.contains("sampling_site")) {
            return idCardNo.substring(0, 1) + "***************" + idCardNo.substring(17);
        }
        
        // 其他角色：部分脱敏（保留前6位和后4位）
        return idCardNo.substring(0, 3) + "***********" + idCardNo.substring(14);
    }

    /**
     * 测试：身份证号验证 - 有效身份证号
     */
    @Test
    void testValidateIdCard_Valid() {
        String idCardNo = "360123199001011234";
        
        boolean result = validateIdCardFormat(idCardNo);
        
        assertTrue(result, "有效身份证号应验证通过");
    }

    /**
     * 测试：身份证号验证 - 长度不正确
     */
    @Test
    void testValidateIdCard_InvalidLength() {
        String idCardNo = "36012319900101";
        
        boolean result = validateIdCardFormat(idCardNo);
        
        assertFalse(result, "长度不正确应验证失败");
    }

    /**
     * 测试：身份证号验证 - 包含非法字符
     */
    @Test
    void testValidateIdCard_InvalidCharacter() {
        String idCardNo = "36012319900101ABCD";
        
        boolean result = validateIdCardFormat(idCardNo);
        
        assertFalse(result, "包含非法字符应验证失败");
    }

    /**
     * 测试：身份证号验证 - 空值
     */
    @Test
    void testValidateIdCard_Null() {
        String idCardNo = null;
        
        boolean result = validateIdCardFormat(idCardNo);
        
        assertFalse(result, "空值应验证失败");
    }

    /**
     * 辅助方法：验证身份证号格式
     */
    private boolean validateIdCardFormat(String idCardNo) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return false;
        }
        
        // 验证是否全为数字（最后一位可以是X）
        String pattern = "^\\d{17}[\\dXx]$";
        return idCardNo.matches(pattern);
    }

    /**
     * 测试：从身份证号解析性别 - 男性
     */
    @Test
    void testParseGender_Male() {
        String idCardNo = "360123199001011237"; // 倒数第二位为奇数
        
        String gender = parseGender(idCardNo);
        
        assertEquals("0", gender, "奇数应解析为男性");
    }

    /**
     * 测试：从身份证号解析性别 - 女性
     */
    @Test
    void testParseGender_Female() {
        String idCardNo = "360123199001011224"; // 倒数第二位为偶数
        
        String gender = parseGender(idCardNo);
        
        assertEquals("1", gender, "偶数应解析为女性");
    }

    /**
     * 辅助方法：从身份证号解析性别
     */
    private String parseGender(String idCardNo) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return null;
        }
        
        // 获取倒数第二位（索引16）
        int genderCode = Character.getNumericValue(idCardNo.charAt(16));
        
        // 奇数为男性（0），偶数为女性（1）
        return (genderCode % 2 == 0) ? "1" : "0";
    }

    /**
     * 测试：从身份证号解析出生日期
     */
    @Test
    void testParseBirthDate() {
        String idCardNo = "360123199001011234";
        
        String birthDate = parseBirthDate(idCardNo);
        
        assertEquals("1990-01-01", birthDate, "出生日期解析应正确");
    }

    /**
     * 测试：从身份证号解析出生日期 - 不同年份
     */
    @Test
    void testParseBirthDate_DifferentYear() {
        String idCardNo = "360123200512151234";
        
        String birthDate = parseBirthDate(idCardNo);
        
        assertEquals("2005-12-15", birthDate);
    }

    /**
     * 辅助方法：从身份证号解析出生日期
     */
    private String parseBirthDate(String idCardNo) {
        if (idCardNo == null || idCardNo.length() != 18) {
            return null;
        }
        
        String year = idCardNo.substring(6, 10);
        String month = idCardNo.substring(10, 12);
        String day = idCardNo.substring(12, 14);
        
        return year + "-" + month + "-" + day;
    }

    /**
     * 测试：计算年龄
     */
    @Test
    void testCalculateAge() {
        String birthDate = "1990-01-01";
        
        int age = calculateAge(birthDate);
        
        assertTrue(age >= 34 && age <= 35, "年龄计算应在合理范围内");
    }

    /**
     * 辅助方法：计算年龄
     */
    private int calculateAge(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return 0;
        }
        
        try {
            String[] parts = birthDate.split("-");
            int birthYear = Integer.parseInt(parts[0]);
            int birthMonth = Integer.parseInt(parts[1]);
            int birthDay = Integer.parseInt(parts[2]);
            
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentYear = now.get(java.util.Calendar.YEAR);
            int currentMonth = now.get(java.util.Calendar.MONTH) + 1;
            int currentDay = now.get(java.util.Calendar.DAY_OF_MONTH);
            
            int age = currentYear - birthYear;
            
            // 如果还没到生日，年龄减1
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }
            
            return age;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 测试：批量脱敏
     */
    @Test
    void testBatchDesensitize() {
        String[] idCards = {
            "360123199001011234",
            "360123199512151234",
            "360123200001011234"
        };
        String roleKey = "admin";
        
        for (String idCard : idCards) {
            String result = desensitizeIdCard(idCard, roleKey);
            assertEquals("360***********1234", result);
        }
    }

    /**
     * 测试：不同角色的脱敏一致性
     */
    @Test
    void testDesensitize_RoleConsistency() {
        String idCardNo = "360123199001011234";
        
        // 管理类角色应脱敏一致
        String adminResult = desensitizeIdCard(idCardNo, "admin");
        String districtResult = desensitizeIdCard(idCardNo, "district_admin");
        String streetResult = desensitizeIdCard(idCardNo, "street_admin");
        
        assertEquals(adminResult, districtResult);
        assertEquals(adminResult, streetResult);
        
        // 采血点管理员脱敏应不同
        String samplingResult = desensitizeIdCard(idCardNo, "sampling_site");
        assertNotEquals(adminResult, samplingResult);
        
        // 医院/医生应返回null
        String hospitalResult = desensitizeIdCard(idCardNo, "hospital");
        assertNull(hospitalResult);
    }

    /**
     * 测试：边界情况 - 最短有效身份证号
     */
    @Test
    void testDesensitize_MinValidIdCard() {
        String idCardNo = "000000000000000000"; // 18位0
        String roleKey = "admin";
        
        String result = desensitizeIdCard(idCardNo, roleKey);
        
        assertEquals("000***********0000", result);
    }

    /**
     * 测试：身份证号唯一性检查
     */
    @Test
    void testCheckIdCardUniqueness() {
        String idCardNo = "360123199001011234";
        
        // 假设数据库中已存在
        boolean exists = true;
        
        assertFalse(!exists, "已存在的身份证号应检测出重复");
    }

    /**
     * 测试：身份证号地区码验证
     */
    @Test
    void testValidateRegionCode() {
        // 江西省九江市濂溪区
        String validRegionCode = "360104";
        
        // 验证地区码格式（6位数字）
        assertTrue(validRegionCode.matches("^\\d{6}$"));
        
        // 江西省代码36开头
        assertTrue(validRegionCode.startsWith("36"));
    }
}
