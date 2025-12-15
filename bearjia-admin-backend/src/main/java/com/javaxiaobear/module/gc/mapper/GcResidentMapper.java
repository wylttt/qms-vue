package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;

/**
 * 居民信息Mapper接口
 * 
 * @author javaxiaobear
 */
public interface GcResidentMapper {
    
    /**
     * 查询居民列表
     * 
     * @param resident 居民信息
     * @return 居民集合
     */
    List<ResidentVO> selectResidentList(GcResident resident);
    
    /**
     * 根据居民ID查询居民信息
     * 
     * @param residentId 居民ID
     * @return 居民信息
     */
    ResidentVO selectResidentById(Long residentId);
    
    /**
     * 根据身份证号查询居民信息
     * 
     * @param idCardNo 身份证号
     * @return 居民信息
     */
    GcResident selectResidentByIdCardNo(String idCardNo);
    
    /**
     * 新增居民信息
     * 
     * @param resident 居民信息
     * @return 结果
     */
    int insertResident(GcResident resident);
    
    /**
     * 修改居民信息
     * 
     * @param resident 居民信息
     * @return 结果
     */
    int updateResident(GcResident resident);
    
    /**
     * 删除居民信息
     * 
     * @param residentId 居民ID
     * @return 结果
     */
    int deleteResidentById(Long residentId);
    
    /**
     * 批量删除居民信息
     * 
     * @param residentIds 需要删除的居民ID
     * @return 结果
     */
    int deleteResidentByIds(Long[] residentIds);
    
    /**
     * 检查居民是否已填写问卷
     * 
     * @param residentId 居民ID
     * @return 问卷记录数量
     */
    int checkResidentHasQuestionnaire(Long residentId);
    
    /**
     * 检查居民是否已预约采血
     * 
     * @param residentId 居民ID
     * @return 预约记录数量
     */
    int checkResidentHasAppointment(Long residentId);
    
    /**
     * 更新居民重点人群标识
     * 
     * @param residentId 居民ID
     * @param isFocusGroup 是否重点人群
     * @return 结果
     */
    int updateResidentFocusGroup(@Param("residentId") Long residentId, @Param("isFocusGroup") Integer isFocusGroup);
}
