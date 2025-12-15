package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 居民信息Mapper接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Mapper
public interface GcResidentMapper {
    
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
     * @param residentIds 需要删除的居民ID数组
     * @return 结果
     */
    int deleteResidentByIds(Long[] residentIds);

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
     * 根据街道统计居民数量
     * 
     * @param street 街道名称
     * @return 居民数量
     */
    int countResidentByStreet(@Param("street") String street);

    /**
     * 根据采血点统计居民预约数量
     * 
     * @param siteId 采血点ID
     * @return 预约数量
     */
    int countResidentBySiteId(@Param("siteId") Long siteId);
}
