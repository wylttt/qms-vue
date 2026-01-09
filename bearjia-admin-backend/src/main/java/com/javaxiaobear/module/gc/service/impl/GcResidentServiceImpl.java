package com.javaxiaobear.module.gc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.base.common.utils.SecurityUtils;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import com.javaxiaobear.module.gc.service.IGcResidentService;
import com.javaxiaobear.module.gc.util.IdCardDesensitizer;
import com.javaxiaobear.module.gc.util.IdCardValidator;

/**
 * 居民信息Service业务层处理
 * 
 * @author javaxiaobear
 */
@Service
public class GcResidentServiceImpl implements IGcResidentService {
    
    private static final Logger log = LoggerFactory.getLogger(GcResidentServiceImpl.class);
    
    @Autowired
    private GcResidentMapper residentMapper;
    
    /**
     * 查询居民列表
     * 
     * @param resident 居民信息
     * @return 居民集合
     */
    @Override
    public List<ResidentVO> selectResidentList(GcResident resident) {
        List<ResidentVO> list = residentMapper.selectResidentList(resident);
        
        // 获取当前用户角色
        String roleKey = getCurrentUserRoleKey();
        
        // 对身份证号进行脱敏
        for (ResidentVO vo : list) {
            vo.setIdCardNo(IdCardDesensitizer.desensitize(vo.getIdCardNo(), roleKey));
        }
        
        return list;
    }
    
    /**
     * 查询居民详情
     * 
     * @param residentId 居民ID
     * @return 居民信息
     */
    @Override
    public ResidentVO selectResidentById(Long residentId) {
        ResidentVO vo = residentMapper.selectResidentById(residentId);
        if (vo != null) {
            // 对身份证号进行脱敏
            String roleKey = getCurrentUserRoleKey();
            vo.setIdCardNo(IdCardDesensitizer.desensitize(vo.getIdCardNo(), roleKey));
        }
        return vo;
    }
    
    /**
     * 新增居民
     * 
     * @param resident 居民信息
     * @return 结果
     */
    @Override
    public int insertResident(GcResident resident) {
        // 1. 验证身份证号格式
        if (!IdCardValidator.validate(resident.getIdCardNo())) {
            throw new ServiceException("身份证号格式不正确");
        }
        
        // 2. 验证身份证号唯一性
        if (!checkIdCardUnique(resident.getIdCardNo())) {
            throw new ServiceException("身份证号已存在");
        }
        
        // 3. 验证性别与身份证号一致性
        if (!IdCardValidator.validateGender(resident.getIdCardNo(), resident.getGender())) {
            throw new ServiceException("性别与身份证号不一致");
        }
        
        // 4. 验证出生日期与身份证号一致性
        if (!IdCardValidator.validateBirthDate(resident.getIdCardNo(), resident.getBirthDate())) {
            throw new ServiceException("出生日期与身份证号不一致");
        }
        
        // 5. 验证手机号格式
        if (!resident.getPhoneNumber().matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("手机号格式不正确");
        }
        
        // 6. 设置默认值
        resident.setIsFocusGroup(0); // 默认非重点人群
        resident.setCreateBy(SecurityUtils.getUsername());
        
        // 7. 插入数据
        return residentMapper.insertResident(resident);
    }
    
    /**
     * 修改居民
     * 
     * @param resident 居民信息
     * @return 结果
     */
    @Override
    public int updateResident(GcResident resident) {
        // 1. 不允许修改身份证号
        GcResident oldResident = residentMapper.selectResidentByIdCardNo(
            residentMapper.selectResidentById(resident.getResidentId()).getIdCardNo()
        );
        if (oldResident != null && !oldResident.getIdCardNo().equals(resident.getIdCardNo())) {
            throw new ServiceException("不允许修改身份证号");
        }
        
        // 2. 验证手机号格式
        if (StringUtils.isNotEmpty(resident.getPhoneNumber()) 
            && !resident.getPhoneNumber().matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("手机号格式不正确");
        }
        
        // 3. 设置更新人
        resident.setUpdateBy(SecurityUtils.getUsername());
        
        // 4. 更新数据
        return residentMapper.updateResident(resident);
    }
    
    /**
     * 删除居民
     * 
     * @param residentId 居民ID
     * @return 结果
     */
    @Override
    public int deleteResidentById(Long residentId) {
        // 1. 检查是否已填写问卷
        if (residentMapper.checkResidentHasQuestionnaire(residentId) > 0) {
            throw new ServiceException("该居民已填写问卷，不允许删除");
        }
        
        // 2. 检查是否已预约采血
        if (residentMapper.checkResidentHasAppointment(residentId) > 0) {
            throw new ServiceException("该居民已预约采血，不允许删除");
        }
        
        // 3. 删除数据
        return residentMapper.deleteResidentById(residentId);
    }
    
    /**
     * 批量删除居民
     * 
     * @param residentIds 居民ID数组
     * @return 结果
     */
    @Override
    public int deleteResidentByIds(Long[] residentIds) {
        for (Long residentId : residentIds) {
            deleteResidentById(residentId);
        }
        return residentIds.length;
    }
    
    /**
     * 校验身份证号唯一性
     * 
     * @param idCardNo 身份证号
     * @return true唯一 false不唯一
     */
    @Override
    public boolean checkIdCardUnique(String idCardNo) {
        GcResident resident = residentMapper.selectResidentByIdCardNo(idCardNo);
        return resident == null;
    }
    
    /**
     * 批量导入居民
     * 
     * @param file Excel文件
     * @param surveyorId 协助录入调查员ID
     * @return 导入结果
     */
    @Override
    public Map<String, Object> importResidents(MultipartFile file, Long surveyorId) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new ArrayList<>();
        
        try {
            // TODO: 实现Excel解析和批量导入
            // 1. 读取Excel文件
            // 2. 逐行解析数据
            // 3. 验证数据
            // 4. 插入数据库
            
            log.info("批量导入居民功能待实现");
            
        } catch (Exception e) {
            log.error("批量导入居民失败", e);
            throw new ServiceException("批量导入失败: " + e.getMessage());
        }
        
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errorMessages", errorMessages);
        return result;
    }
    
    /**
     * 导出居民列表
     * 
     * @param resident 查询条件
     * @param response HTTP响应
     */
    @Override
    public void exportResidents(GcResident resident, HttpServletResponse response) {
        try {
            // TODO: 实现Excel导出
            // 1. 查询数据
            // 2. 构造Excel
            // 3. 写入响应流
            
            log.info("导出居民列表功能待实现");
            
        } catch (Exception e) {
            log.error("导出居民列表失败", e);
            throw new ServiceException("导出失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户角色标识
     * 
     * @return 角色标识
     */
    private String getCurrentUserRoleKey() {
        try {
            return SecurityUtils.getLoginUser().getUser().getRoles()
                .stream()
                .findFirst()
                .map(role -> role.getRoleKey())
                .orElse("");
        } catch (Exception e) {
            log.warn("获取用户角色失败", e);
            return "";
        }
    }
}
