package com.javaxiaobear.module.gc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.javaxiaobear.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcBloodAppointment;
import com.javaxiaobear.module.gc.domain.entity.GcPushLog;
import com.javaxiaobear.module.gc.domain.vo.BloodAppointmentVO;
import com.javaxiaobear.module.gc.mapper.GcBloodAppointmentMapper;
import com.javaxiaobear.module.gc.mapper.GcPushLogMapper;
import com.javaxiaobear.module.gc.service.IBloodSystemPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方系统推送Service实现类
 * 
 * @author javaxiaobear
 * @date 2024-12-15
 */
@Service
public class BloodSystemPushServiceImpl implements IBloodSystemPushService {

    private static final Logger log = LoggerFactory.getLogger(BloodSystemPushServiceImpl.class);

    @Autowired
    private GcBloodAppointmentMapper appointmentMapper;

    @Autowired
    private GcPushLogMapper pushLogMapper;

    // TODO: 从配置文件读取
    private static final String THIRD_SYSTEM_URL = "http://third-party-system.com/api/blood/appointment";
    private static final String THIRD_SYSTEM_SECRET = "your-secret-key";

    /**
     * 推送采血预约信息到第三方系统
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushAppointmentToThirdSystem(Long appointmentId) {
        // 1. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 2. 验证预约状态(仅已确认的预约可以推送)
        if (!"1".equals(appointment.getAppointmentStatus())) {
            throw new ServiceException("仅已确认的预约可以推送");
        }

        // 3. 构建推送数据
        Map<String, Object> pushData = buildPushData(appointment);

        // 4. 创建推送日志
        GcPushLog pushLog = new GcPushLog();
        pushLog.setBusinessType("1"); // 采血预约推送
        pushLog.setBusinessId(appointmentId);
        pushLog.setPushDirection("1"); // 推送到第三方
        pushLog.setRequestUrl(THIRD_SYSTEM_URL);
        pushLog.setRequestMethod("POST");
        pushLog.setRequestBody(JSON.toJSONString(pushData));
        pushLog.setPushTime(new Date());

        long startTime = System.currentTimeMillis();

        try {
            // 5. 调用第三方接口(模拟)
            // TODO: 实际实现需要使用HttpClient调用第三方接口
            Map<String, Object> response = mockThirdSystemCall(pushData);

            long costTime = System.currentTimeMillis() - startTime;

            // 6. 处理响应
            boolean success = (Boolean) response.get("success");
            if (success) {
                // 推送成功
                pushLog.setResponseStatus(200);
                pushLog.setResponseBody(JSON.toJSONString(response));
                pushLog.setCostTime(costTime);
                pushLog.setPushStatus("1"); // 推送成功
                pushLog.setRetryCount(0);

                // 更新预约的推送状态
                appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "1", null, 0);

                // 记录日志
                pushLogMapper.insertPushLog(pushLog);

                log.info("推送预约[{}]到第三方系统成功,耗时{}ms", appointmentId, costTime);
                return true;
            } else {
                // 推送失败
                String errorMsg = (String) response.get("message");
                pushLog.setResponseStatus(200);
                pushLog.setResponseBody(JSON.toJSONString(response));
                pushLog.setCostTime(costTime);
                pushLog.setPushStatus("2"); // 推送失败
                pushLog.setFailReason(errorMsg);
                pushLog.setRetryCount(0);
                pushLog.setNextRetryTime(calculateNextRetryTime(0));

                // 更新预约的推送状态
                appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "2", errorMsg, 0);

                // 记录日志
                pushLogMapper.insertPushLog(pushLog);

                log.error("推送预约[{}]到第三方系统失败:{}", appointmentId, errorMsg);
                return false;
            }
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;

            // 推送异常
            pushLog.setResponseStatus(500);
            pushLog.setResponseBody(e.getMessage());
            pushLog.setCostTime(costTime);
            pushLog.setPushStatus("2"); // 推送失败
            pushLog.setFailReason("推送异常:" + e.getMessage());
            pushLog.setRetryCount(0);
            pushLog.setNextRetryTime(calculateNextRetryTime(0));

            // 更新预约的推送状态
            appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "2", 
                "推送异常:" + e.getMessage(), 0);

            // 记录日志
            pushLogMapper.insertPushLog(pushLog);

            log.error("推送预约[{}]到第三方系统异常", appointmentId, e);
            return false;
        }
    }

    /**
     * 批量推送采血预约信息
     * 
     * @param appointmentIds 预约ID数组
     * @return 成功推送数量
     */
    @Override
    public int batchPushAppointments(Long[] appointmentIds) {
        if (appointmentIds == null || appointmentIds.length == 0) {
            throw new ServiceException("预约ID不能为空");
        }

        int successCount = 0;
        for (Long appointmentId : appointmentIds) {
            try {
                boolean result = pushAppointmentToThirdSystem(appointmentId);
                if (result) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量推送预约[{}]失败", appointmentId, e);
            }
        }

        return successCount;
    }

    /**
     * 重试推送失败的记录
     * 
     * @param appointmentId 预约ID
     * @return 推送结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryPushAppointment(Long appointmentId) {
        // 1. 查询预约信息
        BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
        if (appointment == null) {
            throw new ServiceException("预约信息不存在");
        }

        // 2. 验证推送状态
        if (!"2".equals(appointment.getPushStatus())) {
            throw new ServiceException("该预约推送状态不是失败,无需重试");
        }

        // 3. 验证重试次数
        Integer retryCount = appointment.getRetryCount();
        if (retryCount != null && retryCount >= 3) {
            throw new ServiceException("重试次数已达上限(3次)");
        }

        // 4. 构建推送数据
        Map<String, Object> pushData = buildPushData(appointment);

        // 5. 创建推送日志
        GcPushLog pushLog = new GcPushLog();
        pushLog.setBusinessType("1");
        pushLog.setBusinessId(appointmentId);
        pushLog.setPushDirection("1");
        pushLog.setRequestUrl(THIRD_SYSTEM_URL);
        pushLog.setRequestMethod("POST");
        pushLog.setRequestBody(JSON.toJSONString(pushData));
        pushLog.setPushTime(new Date());
        pushLog.setRetryCount(retryCount + 1);

        long startTime = System.currentTimeMillis();

        try {
            // 6. 调用第三方接口(模拟)
            Map<String, Object> response = mockThirdSystemCall(pushData);

            long costTime = System.currentTimeMillis() - startTime;

            // 7. 处理响应
            boolean success = (Boolean) response.get("success");
            if (success) {
                // 推送成功
                pushLog.setResponseStatus(200);
                pushLog.setResponseBody(JSON.toJSONString(response));
                pushLog.setCostTime(costTime);
                pushLog.setPushStatus("1");

                // 更新预约的推送状态
                appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "1", null, retryCount + 1);

                // 记录日志
                pushLogMapper.insertPushLog(pushLog);

                log.info("重试推送预约[{}]成功,重试次数:{}", appointmentId, retryCount + 1);
                return true;
            } else {
                // 推送失败
                String errorMsg = (String) response.get("message");
                pushLog.setResponseStatus(200);
                pushLog.setResponseBody(JSON.toJSONString(response));
                pushLog.setCostTime(costTime);
                pushLog.setPushStatus("2");
                pushLog.setFailReason(errorMsg);
                pushLog.setNextRetryTime(calculateNextRetryTime(retryCount + 1));

                // 更新预约的推送状态
                appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "2", errorMsg, retryCount + 1);

                // 记录日志
                pushLogMapper.insertPushLog(pushLog);

                log.error("重试推送预约[{}]失败,重试次数:{},失败原因:{}", appointmentId, retryCount + 1, errorMsg);
                return false;
            }
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;

            // 推送异常
            pushLog.setResponseStatus(500);
            pushLog.setResponseBody(e.getMessage());
            pushLog.setCostTime(costTime);
            pushLog.setPushStatus("2");
            pushLog.setFailReason("推送异常:" + e.getMessage());
            pushLog.setNextRetryTime(calculateNextRetryTime(retryCount + 1));

            // 更新预约的推送状态
            appointmentMapper.updatePushStatus(appointmentId, 1, new Date(), "2", 
                "推送异常:" + e.getMessage(), retryCount + 1);

            // 记录日志
            pushLogMapper.insertPushLog(pushLog);

            log.error("重试推送预约[{}]异常,重试次数:{}", appointmentId, retryCount + 1, e);
            return false;
        }
    }

    /**
     * 处理第三方系统回调(采样状态更新)
     * 
     * @param appointmentId 预约ID
     * @param samplingStatus 采样状态
     * @param sampleCode 采血样本编号
     * @param samplerName 采样员姓名
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleThirdSystemCallback(Long appointmentId, String samplingStatus, 
                                            String sampleCode, String samplerName) {
        try {
            // 1. 查询预约信息
            BloodAppointmentVO appointment = appointmentMapper.selectAppointmentDetail(appointmentId);
            if (appointment == null) {
                log.error("回调处理失败:预约[{}]不存在", appointmentId);
                return false;
            }

            // 2. 更新采样状态
            if ("1".equals(samplingStatus)) {
                appointmentMapper.updateSamplingStatus(appointmentId, "1", new Date(), samplerName, sampleCode);
                log.info("回调处理成功:更新预约[{}]采样状态为已采样", appointmentId);
            }

            // 3. 记录回调日志
            GcPushLog pushLog = new GcPushLog();
            pushLog.setBusinessType("1");
            pushLog.setBusinessId(appointmentId);
            pushLog.setPushDirection("2"); // 接收第三方回调
            pushLog.setRequestBody(String.format("samplingStatus=%s,sampleCode=%s,samplerName=%s", 
                samplingStatus, sampleCode, samplerName));
            pushLog.setPushStatus("1");
            pushLog.setPushTime(new Date());
            pushLogMapper.insertPushLog(pushLog);

            return true;
        } catch (Exception e) {
            log.error("处理第三方回调异常", e);
            return false;
        }
    }

    /**
     * 验证回调签名
     * 
     * @param signature 签名
     * @param timestamp 时间戳
     * @param data 数据
     * @return 验证结果
     */
    @Override
    public boolean validateCallbackSignature(String signature, String timestamp, String data) {
        try {
            // 1. 验证时间戳(5分钟内有效)
            long currentTime = System.currentTimeMillis();
            long requestTime = Long.parseLong(timestamp);
            if (Math.abs(currentTime - requestTime) > 5 * 60 * 1000) {
                log.error("回调签名验证失败:时间戳过期");
                return false;
            }

            // 2. 计算签名
            String expectedSignature = calculateSignature(timestamp, data, THIRD_SYSTEM_SECRET);

            // 3. 验证签名
            if (!expectedSignature.equals(signature)) {
                log.error("回调签名验证失败:签名不匹配");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("回调签名验证异常", e);
            return false;
        }
    }

    /**
     * 构建推送数据
     * 
     * @param appointment 预约信息
     * @return 推送数据
     */
    private Map<String, Object> buildPushData(BloodAppointmentVO appointment) {
        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getAppointmentId());
        data.put("residentName", appointment.getResidentName());
        data.put("idCardNo", appointment.getIdCardNo());
        data.put("gender", appointment.getGender());
        data.put("age", appointment.getAge());
        data.put("phoneNumber", appointment.getPhoneNumber());
        data.put("siteName", appointment.getSiteName());
        data.put("siteAddress", appointment.getSiteAddress());
        data.put("appointmentDate", appointment.getAppointmentDate());
        data.put("appointmentPeriod", appointment.getAppointmentPeriod());
        data.put("timestamp", System.currentTimeMillis());

        // 添加签名
        String signature = calculateSignature(
            String.valueOf(System.currentTimeMillis()),
            JSON.toJSONString(data),
            THIRD_SYSTEM_SECRET
        );
        data.put("signature", signature);

        return data;
    }

    /**
     * 模拟第三方系统调用
     * 
     * @param pushData 推送数据
     * @return 响应数据
     */
    private Map<String, Object> mockThirdSystemCall(Map<String, Object> pushData) {
        // TODO: 实际实现需要使用HttpClient调用第三方接口
        // 这里模拟返回成功响应
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "推送成功");
        response.put("data", null);
        return response;
    }

    /**
     * 计算下次重试时间(指数退避策略)
     * 
     * @param retryCount 当前重试次数
     * @return 下次重试时间
     */
    private Date calculateNextRetryTime(int retryCount) {
        // 指数退避: 2^retryCount 分钟后重试
        long delayMinutes = (long) Math.pow(2, retryCount);
        long delayMillis = delayMinutes * 60 * 1000;
        return new Date(System.currentTimeMillis() + delayMillis);
    }

    /**
     * 计算签名
     * 
     * @param timestamp 时间戳
     * @param data 数据
     * @param secret 密钥
     * @return 签名
     */
    private String calculateSignature(String timestamp, String data, String secret) {
        try {
            String signStr = timestamp + data + secret;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(signStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("计算签名异常", e);
            return "";
        }
    }
}
