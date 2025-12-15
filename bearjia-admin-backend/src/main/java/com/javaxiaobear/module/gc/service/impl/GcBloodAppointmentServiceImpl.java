package com.javaxiaobear.module.gc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.javaxiaobear.common.utils.SecurityUtils;
import com.javaxiaobear.module.gc.domain.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.GcPushLog;
import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import com.javaxiaobear.module.gc.service.IGcBloodAppointmentService;
import com.javaxiaobear.module.gc.service.IGcPushLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采血预约Service业务层处理
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class GcBloodAppointmentServiceImpl implements IGcBloodAppointmentService {
    
    private static final Logger log = LoggerFactory.getLogger(GcBloodAppointmentServiceImpl.class);
    
    @Autowired
    private GcBloodAppointmentMapper appointmentMapper;
    
    @Autowired
    private GcResidentMapper residentMapper;
    
    @Autowired
    private IGcPushLogService pushLogService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // 第三方系统推送URL(从配置文件读取)
    @Value("${gc.third-system.push-url:http://third-system.example.com/api/blood/appointment}")
    private String thirdSystemPushUrl;
    
    // 最大重试次数
    @Value("${gc.third-system.max-retry:3}")
    private int maxRetryTimes;
    
    /**
     * 查询采血预约
     * 
     * @param appointmentId 采血预约主键
     * @return 采血预约
     */
    @Override
    public GcBloodAppointment selectAppointmentById(Long appointmentId) {
        return appointmentMapper.selectAppointmentById(appointmentId);
    }
    
    /**
     * 查询采血预约列表
     * 
     * @param appointment 采血预约
     * @return 采血预约
     */
    @Override
    public List<GcBloodAppointment> selectAppointmentList(GcBloodAppointment appointment) {
        return appointmentMapper.selectAppointmentList(appointment);
    }
    
    /**
     * 新增采血预约
     * 
     * @param appointment 采血预约
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAppointment(GcBloodAppointment appointment) {
        // 验证居民是否存在
        GcResident resident = residentMapper.selectResidentById(appointment.getResidentId());
        if (resident == null) {
            throw new RuntimeException("居民不存在");
        }
        
        // 检查居民是否已经有未完成的预约
        GcBloodAppointment query = new GcBloodAppointment();
        query.setResidentId(appointment.getResidentId());
        query.setAppointmentStatus("pending");
        List<GcBloodAppointment> existingAppointments = appointmentMapper.selectAppointmentList(query);
        if (!existingAppointments.isEmpty()) {
            throw new RuntimeException("该居民已有待完成的预约，请先完成或取消");
        }
        
        // 设置默认值
        appointment.setAppointmentStatus("pending");
        appointment.setPushStatus("pending");
        appointment.setRetryTimes(0);
        appointment.setSamplingStatus("0");
        appointment.setCreateBy(SecurityUtils.getUsername());
        
        int result = appointmentMapper.insertAppointment(appointment);
        
        // 创建成功后，尝试推送到第三方系统
        if (result > 0) {
            pushToThirdSystem(appointment.getAppointmentId());
        }
        
        return result;
    }
    
    /**
     * 修改采血预约
     * 
     * @param appointment 采血预约
     * @return 结果
     */
    @Override
    public int updateAppointment(GcBloodAppointment appointment) {
        appointment.setUpdateBy(SecurityUtils.getUsername());
        return appointmentMapper.updateAppointment(appointment);
    }
    
    /**
     * 批量删除采血预约
     * 
     * @param appointmentIds 需要删除的采血预约主键
     * @return 结果
     */
    @Override
    public int deleteAppointmentByIds(Long[] appointmentIds) {
        return appointmentMapper.deleteAppointmentByIds(appointmentIds);
    }
    
    /**
     * 删除采血预约信息
     * 
     * @param appointmentId 采血预约主键
     * @return 结果
     */
    @Override
    public int deleteAppointmentById(Long appointmentId) {
        return appointmentMapper.deleteAppointmentById(appointmentId);
    }
    
    /**
     * 根据居民ID查询预约记录
     * 
     * @param residentId 居民ID
     * @return 采血预约列表
     */
    @Override
    public List<GcBloodAppointment> selectAppointmentByResidentId(Long residentId) {
        return appointmentMapper.selectAppointmentByResidentId(residentId);
    }
    
    /**
     * 根据采血点ID查询预约记录
     * 
     * @param siteId 采血点ID
     * @return 采血预约列表
     */
    @Override
    public List<GcBloodAppointment> selectAppointmentBySiteId(Long siteId) {
        return appointmentMapper.selectAppointmentBySiteId(siteId);
    }
    
    /**
     * 取消预约
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelAppointment(Long appointmentId) {
        GcBloodAppointment appointment = appointmentMapper.selectAppointmentById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        
        if (!"pending".equals(appointment.getAppointmentStatus())) {
            throw new RuntimeException("只能取消待完成的预约");
        }
        
        appointment.setAppointmentStatus("cancelled");
        appointment.setUpdateBy(SecurityUtils.getUsername());
        
        return appointmentMapper.updateAppointment(appointment);
    }
    
    /**
     * 推送预约信息到第三方系统
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean pushToThirdSystem(Long appointmentId) {
        long startTime = System.currentTimeMillis();
        
        GcBloodAppointment appointment = appointmentMapper.selectAppointmentById(appointmentId);
        if (appointment == null) {
            log.error("预约不存在: {}", appointmentId);
            return false;
        }
        
        // 检查是否已推送成功
        if ("pushed".equals(appointment.getPushStatus())) {
            log.info("预约已推送成功，无需重复推送: {}", appointmentId);
            return true;
        }
        
        // 检查重试次数
        if (appointment.getRetryTimes() >= maxRetryTimes) {
            log.warn("预约推送已达最大重试次数: {}, retryTimes: {}", appointmentId, appointment.getRetryTimes());
            return false;
        }
        
        // 更新推送状态为推送中
        appointmentMapper.updatePushStatus(appointmentId, "pushing", null, null);
        
        // 构建推送数据
        Map<String, Object> pushData = new HashMap<>();
        pushData.put("appointmentId", appointment.getAppointmentId());
        pushData.put("residentId", appointment.getResidentId());
        pushData.put("residentName", appointment.getResidentName());
        pushData.put("contactPhone", appointment.getContactPhone());
        pushData.put("siteId", appointment.getSiteId());
        pushData.put("siteName", appointment.getSiteName());
        pushData.put("appointmentTime", appointment.getAppointmentTime());
        
        String requestBody = JSON.toJSONString(pushData);
        
        // 创建推送日志
        GcPushLog pushLog = new GcPushLog();
        pushLog.setAppointmentId(appointmentId);
        pushLog.setRequestUrl(thirdSystemPushUrl);
        pushLog.setRequestBody(requestBody);
        pushLog.setRetryTimes(appointment.getRetryTimes());
        
        try {
            // 设置HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送HTTP POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                thirdSystemPushUrl, 
                requestEntity, 
                String.class
            );
            
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录响应信息
            pushLog.setResponseCode(response.getStatusCodeValue());
            pushLog.setResponseBody(response.getBody());
            pushLog.setCostTime((int) costTime);
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject responseData = JSON.parseObject(response.getBody());
                String thirdSystemId = responseData.getString("thirdSystemId");
                String barcodeNumber = responseData.getString("barcodeNumber");
                
                // 更新推送成功状态
                appointmentMapper.updatePushStatus(appointmentId, "pushed", thirdSystemId, barcodeNumber);
                
                pushLog.setPushStatus("success");
                pushLogService.insertPushLog(pushLog);
                
                log.info("预约推送成功: {}, thirdSystemId: {}, barcode: {}", 
                    appointmentId, thirdSystemId, barcodeNumber);
                return true;
            } else {
                // 推送失败
                pushLog.setPushStatus("failed");
                pushLog.setErrorMessage("HTTP状态码: " + response.getStatusCodeValue());
                pushLogService.insertPushLog(pushLog);
                
                appointmentMapper.updatePushStatus(appointmentId, "failed", null, null);
                
                log.error("预约推送失败: {}, HTTP状态码: {}", appointmentId, response.getStatusCodeValue());
                return false;
            }
            
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            
            // 记录异常信息
            pushLog.setPushStatus("failed");
            pushLog.setErrorMessage(e.getMessage());
            pushLog.setCostTime((int) costTime);
            pushLogService.insertPushLog(pushLog);
            
            // 更新推送失败状态
            appointmentMapper.updatePushStatus(appointmentId, "failed", null, null);
            
            log.error("预约推送异常: {}, error: {}", appointmentId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 批量推送待推送的预约
     * 
     * @return 推送成功数量
     */
    @Override
    public int batchPushPendingAppointments() {
        List<GcBloodAppointment> pendingList = appointmentMapper.selectPendingPushAppointments();
        
        int successCount = 0;
        for (GcBloodAppointment appointment : pendingList) {
            boolean result = pushToThirdSystem(appointment.getAppointmentId());
            if (result) {
                successCount++;
            }
        }
        
        log.info("批量推送待推送预约完成: total={}, success={}", pendingList.size(), successCount);
        return successCount;
    }
    
    /**
     * 重试推送失败的预约
     * 
     * @return 推送成功数量
     */
    @Override
    public int retryFailedAppointments() {
        List<GcBloodAppointment> failedList = appointmentMapper.selectFailedPushAppointments(maxRetryTimes);
        
        int successCount = 0;
        for (GcBloodAppointment appointment : failedList) {
            boolean result = pushToThirdSystem(appointment.getAppointmentId());
            if (result) {
                successCount++;
            }
        }
        
        log.info("重试推送失败预约完成: total={}, success={}", failedList.size(), successCount);
        return successCount;
    }
    
    /**
     * 接收第三方系统回调更新采样状态
     * 
     * @param thirdSystemId 第三方系统预约ID
     * @param samplingStatus 采样状态
     * @param barcodeNumber 条码编号
     * @return 结果
     */
    @Override
    @Transactional
    public boolean receiveThirdSystemCallback(String thirdSystemId, String samplingStatus, String barcodeNumber) {
        // 根据第三方系统ID查询预约
        GcBloodAppointment query = new GcBloodAppointment();
        query.setThirdSystemId(thirdSystemId);
        List<GcBloodAppointment> appointments = appointmentMapper.selectAppointmentList(query);
        
        if (appointments.isEmpty()) {
            log.error("未找到对应的预约记录: thirdSystemId={}", thirdSystemId);
            return false;
        }
        
        GcBloodAppointment appointment = appointments.get(0);
        
        // 更新采样状态
        int result = appointmentMapper.updateSamplingStatus(appointment.getAppointmentId(), samplingStatus);
        
        if (result > 0) {
            // 同步更新居民表的采样状态
            GcResident resident = residentMapper.selectResidentById(appointment.getResidentId());
            if (resident != null) {
                resident.setSamplingStatus(samplingStatus);
                if ("1".equals(samplingStatus)) {
                    resident.setSamplingTime(new Date());
                }
                residentMapper.updateResident(resident);
            }
            
            log.info("接收第三方系统回调成功: appointmentId={}, thirdSystemId={}, samplingStatus={}, barcode={}", 
                appointment.getAppointmentId(), thirdSystemId, samplingStatus, barcodeNumber);
            return true;
        }
        
        return false;
    }
}
