package com.javaxiaobear.module.gc.service.impl;

import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.entity.GcSamplingSite;
import com.javaxiaobear.module.gc.domain.vo.BloodAppointmentVO;
import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcSamplingSiteMapper;
import com.javaxiaobear.module.gc.service.IGcBloodAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 采血预约Service实现类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcBloodAppointmentServiceImpl implements IGcBloodAppointmentService {

    @Autowired
    private GcBloodAppointmentMapper appointmentMapper;

    @Autowired
    private GcSamplingSiteMapper samplingSiteMapper;

    /**
     * 查询采血预约列表
     * 
     * @param appointment 查询条件
     * @return 预约列表
     */
    @Override
    public List<BloodAppointmentVO> selectAppointmentList(GcBloodAppointment appointment) {
        return appointmentMapper.selectAppointmentList(appointment);
    }

    /**
     * 查询采血预约详情
     * 
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    @Override
    public BloodAppointmentVO selectAppointmentDetail(Long appointmentId) {
        if (appointmentId == null) {
            throw new ServiceException("预约ID不能为空");
        }
        return appointmentMapper.selectAppointmentDetail(appointmentId);
    }

    /**
     * 新增采血预约
     * 
     * @param appointment 预约信息
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAppointment(GcBloodAppointment appointment) {
        // 1. 验证必填字段
        validateAppointment(appointment);

        // 2. 检查时间冲突
        int conflictCount = appointmentMapper.checkTimeConflict(
            appointment.getResidentId(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentPeriod(),
            null
        );
        if (conflictCount > 0) {
            throw new ServiceException("该居民在此时间段已有预约,请选择其他时间");
        }

        // 3. 设置默认值
        appointment.setAppointmentStatus("0"); // 待确认
        appointment.setSamplingStatus("0"); // 未采样
        appointment.setIsPushed(0); // 未推送
        appointment.setPushStatus("0"); // 未推送
        appointment.setRetryCount(0); // 重试次数为0

        // 4. 插入预约记录
        return appointmentMapper.insertAppointment(appointment);
    }

    /**
     * 修改采血预约
     * 
     * @param appointment 预约信息
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAppointment(GcBloodAppointment appointment) {
        // 1. 验证必填字段
        if (appointment.getAppointmentId() == null) {
            throw new ServiceException("预约ID不能为空");
        }

        // 2. 查询原预约信息
        BloodAppointmentVO oldAppointment = appointmentMapper.selectAppointmentDetail(appointment.getAppointmentId());
        if (oldAppointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 3. 仅允许修改待确认和已确认状态的预约
        if (!"0".equals(oldAppointment.getAppointmentStatus()) && !"1".equals(oldAppointment.getAppointmentStatus())) {
            throw new ServiceException("当前状态不允许修改");
        }

        // 4. 如果修改了时间,检查时间冲突
        if (appointment.getAppointmentDate() != null || appointment.getAppointmentPeriod() != null) {
            Date checkDate = appointment.getAppointmentDate() != null ? 
                appointment.getAppointmentDate() : oldAppointment.getAppointmentDate();
            String checkPeriod = appointment.getAppointmentPeriod() != null ? 
                appointment.getAppointmentPeriod() : oldAppointment.getAppointmentPeriod();

            int conflictCount = appointmentMapper.checkTimeConflict(
                oldAppointment.getResidentId(),
                checkDate,
                checkPeriod,
                appointment.getAppointmentId()
            );
            if (conflictCount > 0) {
                throw new ServiceException("该居民在此时间段已有预约,请选择其他时间");
            }
        }

        // 5. 更新预约记录
        return appointmentMapper.updateAppointment(appointment);
    }

    /**
     * 删除采血预约
     * 
     * @param appointmentId 预约ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointmentById(Long appointmentId) {
        // 1. 验证预约ID
        if (appointmentId == null) {
            throw new ServiceException("预约ID不能为空");
        }

        // 2. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 3. 仅允许删除待确认状态的预约
        if (!"0".equals(appointment.getAppointmentStatus())) {
            throw new ServiceException("仅允许删除待确认状态的预约");
        }

        // 4. 删除预约记录
        return appointmentMapper.deleteAppointmentById(appointmentId);
    }

    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 预约ID数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointmentByIds(Long[] appointmentIds) {
        if (appointmentIds == null || appointmentIds.length == 0) {
            throw new ServiceException("预约ID不能为空");
        }

        // 逐个验证并删除
        for (Long appointmentId : appointmentIds) {
            BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
            if (appointment != null && !"0".equals(appointment.getAppointmentStatus())) {
                throw new ServiceException("存在非待确认状态的预约,不允许批量删除");
            }
        }

        return appointmentMapper.deleteAppointmentByIds(appointmentIds);
    }

    /**
     * 确认预约
     * 
     * @param appointmentId 预约ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmAppointment(Long appointmentId, Long operatorId, String operatorName) {
        // 1. 验证参数
        if (appointmentId == null) {
            throw new ServiceException("预约ID不能为空");
        }

        // 2. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 3. 验证状态
        if (!"0".equals(appointment.getAppointmentStatus())) {
            throw new ServiceException("当前状态不允许确认");
        }

        // 4. 更新状态为已确认
        return appointmentMapper.updateAppointmentStatus(appointmentId, "1", operatorId, operatorName);
    }

    /**
     * 取消预约
     * 
     * @param appointmentId 预约ID
     * @param cancelReason 取消原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelAppointment(Long appointmentId, String cancelReason, Long operatorId, String operatorName) {
        // 1. 验证参数
        if (appointmentId == null) {
            throw new ServiceException("预约ID不能为空");
        }

        // 2. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 3. 验证状态(仅待确认和已确认的预约可以取消)
        if (!"0".equals(appointment.getAppointmentStatus()) && !"1".equals(appointment.getAppointmentStatus())) {
            throw new ServiceException("当前状态不允许取消");
        }

        // 4. 更新状态为已取消,并设置取消原因
        GcBloodAppointment updateEntity = new GcBloodAppointment();
        updateEntity.setAppointmentId(appointmentId);
        updateEntity.setAppointmentStatus("3");
        updateEntity.setCancelReason(cancelReason);
        updateEntity.setOperatorId(operatorId);
        updateEntity.setOperatorName(operatorName);

        return appointmentMapper.updateAppointment(updateEntity);
    }

    /**
     * 更新采样状态
     * 
     * @param appointmentId 预约ID
     * @param samplerName 采样员姓名
     * @param sampleCode 采血样本编号
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSamplingStatus(Long appointmentId, String samplerName, String sampleCode) {
        // 1. 验证参数
        if (appointmentId == null) {
            throw new ServiceException("预约ID不能为空");
        }
        if (samplerName == null || samplerName.trim().isEmpty()) {
            throw new ServiceException("采样员姓名不能为空");
        }
        if (sampleCode == null || sampleCode.trim().isEmpty()) {
            throw new ServiceException("采血样本编号不能为空");
        }

        // 2. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 3. 验证状态(仅已确认的预约可以采样)
        if (!"1".equals(appointment.getAppointmentStatus())) {
            throw new ServiceException("仅已确认的预约可以进行采样");
        }

        // 4. 更新采样状态
        return appointmentMapper.updateSamplingStatus(
            appointmentId, 
            "1", 
            new Date(), 
            samplerName, 
            sampleCode
        );
    }

    /**
     * 批量预约
     * 
     * @param residentIds 居民ID列表
     * @param appointmentSiteId 采血点ID
     * @param appointmentDate 预约日期
     * @param appointmentPeriod 预约时间段
     * @return 成功预约数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAppointment(List<Long> residentIds, Long appointmentSiteId, 
                                Date appointmentDate, String appointmentPeriod) {
        // 1. 验证参数
        if (residentIds == null || residentIds.isEmpty()) {
            throw new ServiceException("居民ID列表不能为空");
        }
        if (appointmentSiteId == null) {
            throw new ServiceException("采血点ID不能为空");
        }
        if (appointmentDate == null) {
            throw new ServiceException("预约日期不能为空");
        }
        if (appointmentPeriod == null || appointmentPeriod.trim().isEmpty()) {
            throw new ServiceException("预约时间段不能为空");
        }

        // 2. 批量创建预约
        int successCount = 0;
        for (Long residentId : residentIds) {
            // 检查时间冲突
            int conflictCount = appointmentMapper.checkTimeConflict(
                residentId, appointmentDate, appointmentPeriod, null
            );
            if (conflictCount > 0) {
                continue; // 跳过已有预约的居民
            }

            // 创建预约
            GcBloodAppointment appointment = new GcBloodAppointment();
            appointment.setResidentId(residentId);
            appointment.setAppointmentSiteId(appointmentSiteId);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentPeriod(appointmentPeriod);
            appointment.setAppointmentStatus("0");
            appointment.setSamplingStatus("0");
            appointment.setIsPushed(0);
            appointment.setPushStatus("0");
            appointment.setRetryCount(0);

            int result = appointmentMapper.insertAppointment(appointment);
            if (result > 0) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 导出预约列表
     * 
     * @param appointment 查询条件
     * @return 预约列表
     */
    @Override
    public List<BloodAppointmentVO> exportAppointmentList(GcBloodAppointment appointment) {
        return appointmentMapper.selectAppointmentList(appointment);
    }

    /**
     * 验证预约信息
     * 
     * @param appointment 预约信息
     */
    private void validateAppointment(GcBloodAppointment appointment) {
        if (appointment.getResidentId() == null) {
            throw new ServiceException("居民ID不能为空");
        }
        if (appointment.getAppointmentSiteId() == null) {
            throw new ServiceException("采血点ID不能为空");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new ServiceException("预约日期不能为空");
        }
        if (appointment.getAppointmentPeriod() == null || appointment.getAppointmentPeriod().trim().isEmpty()) {
            throw new ServiceException("预约时间段不能为空");
        }
        if (!"1".equals(appointment.getAppointmentPeriod()) && !"2".equals(appointment.getAppointmentPeriod())) {
            throw new ServiceException("预约时间段必须为1(上午)或2(下午)");
        }
    }

    /**
     * 检查采血点容量
     * 
     * @param siteId 采血点ID
     * @param appointmentDate 预约日期
     * @return 容量信息
     */
    @Override
    public Map<String, Object> checkCapacity(Long siteId, Date appointmentDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询采血点信息
        GcSamplingSite site = samplingSiteMapper.selectById(siteId);
        if (site == null) {
            throw new ServiceException("采血点不存在");
        }
        
        // 2. 获取每日最大容量（默认100）
        Integer maxCapacity = site.getDailyCapacity() != null ? site.getDailyCapacity() : 100;
        
        // 3. 统计当前日期的预约数量（仅统计待确认和已确认状态）
        int currentCount = appointmentMapper.countAppointmentByDate(siteId, appointmentDate);
        
        // 4. 判断是否满额
        boolean isFull = currentCount >= maxCapacity;
        
        result.put("isFull", isFull);
        result.put("currentCount", currentCount);
        result.put("maxCapacity", maxCapacity);
        result.put("remainingCapacity", Math.max(0, maxCapacity - currentCount));
        
        return result;
    }

    /**
     * 获取可用时间段
     * 
     * @param siteId 采血点ID
     * @param appointmentDate 预约日期
     * @return 可用时间段列表
     */
    @Override
    public List<Map<String, Object>> getAvailableTimeSlots(Long siteId, Date appointmentDate) {
        List<Map<String, Object>> slots = new ArrayList<>();
        
        // 检查整体容量
        Map<String, Object> capacityInfo = checkCapacity(siteId, appointmentDate);
        boolean isFull = (boolean) capacityInfo.get("isFull");
        
        // 如果已满额,返回空列表
        if (isFull) {
            return slots;
        }
        
        // 返回可用时间段（上午/下午）
        Map<String, Object> morningSlot = new HashMap<>();
        morningSlot.put("period", "1");
        morningSlot.put("label", "上午 08:00-12:00");
        morningSlot.put("available", true);
        slots.add(morningSlot);
        
        Map<String, Object> afternoonSlot = new HashMap<>();
        afternoonSlot.put("period", "2");
        afternoonSlot.put("label", "下午 14:00-18:00");
        afternoonSlot.put("available", true);
        slots.add(afternoonSlot);
        
        return slots;
    }
}
