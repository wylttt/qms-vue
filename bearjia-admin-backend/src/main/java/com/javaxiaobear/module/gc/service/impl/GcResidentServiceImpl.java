package com.javaxiaobear.module.gc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.domain.vo.ResidentVO;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import com.javaxiaobear.module.gc.service.IGcResidentService;

/**
 * 居民信息Service业务层处理
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Service
public class GcResidentServiceImpl implements IGcResidentService {
    
    @Autowired
    private GcResidentMapper residentMapper;

    /**
     * 查询居民信息列表
     * 
     * @param resident 居民信息
     * @return 居民信息集合
     */
    @Override
    public List<ResidentVO> selectResidentList(GcResident resident) {
        List<ResidentVO> list = residentMapper.selectResidentList(resident);
        
        // TODO: 根据当前登录用户角色判断是否需要脱敏身份证号
        // 采血点管理员和医生角色不可查看身份证号,需要脱敏处理
        // 超级管理员、区级管理员、街道管理员可以查看完整身份证号
        // 脱敏规则: 保留前6位和后4位,中间8位替换为*
        // 示例: 362301199001011234 -> 362301********1234
        
        return list;
    }

    /**
     * 查询居民信息详情
     * 
     * @param residentId 居民ID
     * @return 居民信息
     */
    @Override
    public GcResident selectResidentById(Long residentId) {
        return residentMapper.selectResidentById(residentId);
    }

    /**
     * 新增居民信息
     * 
     * @param resident 居民信息
     * @return 结果
     */
    @Override
    public int insertResident(GcResident resident) {
        // 验证身份证号唯一性
        if (StringUtils.isNotEmpty(resident.getIdCardNo())) {
            GcResident existResident = residentMapper.selectResidentByIdCardNo(resident.getIdCardNo());
            if (existResident != null) {
                throw new RuntimeException("该身份证号已存在");
            }
            
            // 验证身份证号格式 (18位身份证号)
            if (!resident.getIdCardNo().matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
                throw new RuntimeException("身份证号格式不正确");
            }
        }
        
        // 验证手机号格式
        if (StringUtils.isNotEmpty(resident.getContactPhone())) {
            if (!resident.getContactPhone().matches("^1[3-9]\\d{9}$")) {
                throw new RuntimeException("手机号格式不正确");
            }
        }
        
        // 默认采样状态为未采样
        if (StringUtils.isEmpty(resident.getSamplingStatus())) {
            resident.setSamplingStatus("0");
        }
        
        // 默认非重点人群
        if (resident.getIsFocusGroup() == null) {
            resident.setIsFocusGroup(0);
        }
        
        // 默认创建来源为居民
        if (StringUtils.isEmpty(resident.getCreateSource())) {
            resident.setCreateSource("resident");
        }
        
        return residentMapper.insertResident(resident);
    }

    /**
     * 修改居民信息
     * 
     * @param resident 居民信息
     * @return 结果
     */
    @Override
    public int updateResident(GcResident resident) {
        // 不允许修改身份证号
        resident.setIdCardNo(null);
        
        // 验证手机号格式
        if (StringUtils.isNotEmpty(resident.getContactPhone())) {
            if (!resident.getContactPhone().matches("^1[3-9]\\d{9}$")) {
                throw new RuntimeException("手机号格式不正确");
            }
        }
        
        return residentMapper.updateResident(resident);
    }

    /**
     * 批量删除居民信息
     * 
     * @param residentIds 需要删除的居民ID数组
     * @return 结果
     */
    @Override
    public int deleteResidentByIds(Long[] residentIds) {
        // TODO: 检查是否有问卷记录或筛查结果,存在则不允许删除
        return residentMapper.deleteResidentByIds(residentIds);
    }

    /**
     * 删除居民信息
     * 
     * @param residentId 居民ID
     * @return 结果
     */
    @Override
    public int deleteResidentById(Long residentId) {
        // TODO: 检查是否有问卷记录或筛查结果,存在则不允许删除
        return residentMapper.deleteResidentById(residentId);
    }

    /**
     * 统计居民总数
     * 
     * @param resident 查询条件
     * @return 总数
     */
    @Override
    public int countResident(GcResident resident) {
        return residentMapper.countResident(resident);
    }

    /**
     * 统计重点人群数量
     * 
     * @param resident 查询条件
     * @return 重点人群数量
     */
    @Override
    public int countFocusGroup(GcResident resident) {
        return residentMapper.countFocusGroup(resident);
    }

    /**
     * 导出居民信息
     * 
     * @param resident 查询条件
     * @return 居民信息列表
     */
    @Override
    public List<ResidentVO> exportResident(GcResident resident) {
        return residentMapper.selectResidentList(resident);
    }
}
