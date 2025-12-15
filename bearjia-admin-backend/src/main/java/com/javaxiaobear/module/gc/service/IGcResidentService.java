package com.javaxiaobear.module.gc.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;

/**
 * 居民信息Service接口
 * 
 * @author javaxiaobear
 */
public interface IGcResidentService {
    
    /**
     * 查询居民列表
     * 
     * @param resident 居民信息
     * @return 居民集合
     */
    List<ResidentVO> selectResidentList(GcResident resident);
    
    /**
     * 查询居民详情
     * 
     * @param residentId 居民ID
     * @return 居民信息
     */
    ResidentVO selectResidentById(Long residentId);
    
    /**
     * 新增居民
     * 
     * @param resident 居民信息
     * @return 结果
     */
    int insertResident(GcResident resident);
    
    /**
     * 修改居民
     * 
     * @param resident 居民信息
     * @return 结果
     */
    int updateResident(GcResident resident);
    
    /**
     * 删除居民
     * 
     * @param residentId 居民ID
     * @return 结果
     */
    int deleteResidentById(Long residentId);
    
    /**
     * 批量删除居民
     * 
     * @param residentIds 居民ID数组
     * @return 结果
     */
    int deleteResidentByIds(Long[] residentIds);
    
    /**
     * 校验身份证号唯一性
     * 
     * @param idCardNo 身份证号
     * @return true唯一 false不唯一
     */
    boolean checkIdCardUnique(String idCardNo);
    
    /**
     * 批量导入居民
     * 
     * @param file Excel文件
     * @param surveyorId 协助录入调查员ID
     * @return 导入结果
     */
    Map<String, Object> importResidents(MultipartFile file, Long surveyorId);
    
    /**
     * 导出居民列表
     * 
     * @param resident 查询条件
     * @param response HTTP响应
     */
    void exportResidents(GcResident resident, HttpServletResponse response);
}
