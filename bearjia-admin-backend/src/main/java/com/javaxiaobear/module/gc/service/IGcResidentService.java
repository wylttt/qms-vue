package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;

import java.util.List;

/**
 * 居民信息Service接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
public interface IGcResidentService {
    
    /**
     * 查询居民信息列表
     * 
     * @param resident 居民信息
     * @return 居民信息集合
     */
    List<ResidentVO> selectResidentList(GcResident resident);

    /**
     * 查询居民信息详情
     * 
     * @param residentId 居民ID
     * @return 居民信息
     */
    GcResident selectResidentById(Long residentId);

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
     * 批量删除居民信息
     * 
     * @param residentIds 需要删除的居民ID数组
     * @return 结果
     */
    int deleteResidentByIds(Long[] residentIds);

    /**
     * 删除居民信息
     * 
     * @param residentId 居民ID
     * @return 结果
     */
    int deleteResidentById(Long residentId);

    /**
     * 统计居民总数
     * 
     * @param resident 查询条件
     * @return 总数
     */
    int countResident(GcResident resident);

    /**
     * 统计重点人群数量
     * 
     * @param resident 查询条件
     * @return 重点人群数量
     */
    int countFocusGroup(GcResident resident);

    /**
     * 导出居民信息
     * 
     * @param resident 查询条件
     * @return 居民信息列表
     */
    List<ResidentVO> exportResident(GcResident resident);
}
