package com.javaxiaobear.module.gc.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javaxiaobear.base.common.exception.ServiceException;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.domain.vo.QuestionnaireRecordVO;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireRecordMapper;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireTemplateMapper;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireRecordService;
import com.javaxiaobear.module.gc.util.QuestionnaireScoreCalculator;
import com.javaxiaobear.module.gc.util.IdCardDesensitizer;

/**
 * 问卷记录Service业务层处理
 * 
 * @author javaxiaobear
 */
@Service
public class GcQuestionnaireRecordServiceImpl implements IGcQuestionnaireRecordService {
    
    @Autowired
    private GcQuestionnaireRecordMapper recordMapper;
    
    @Autowired
    private GcQuestionnaireTemplateMapper templateMapper;
    
    @Autowired
    private GcResidentMapper residentMapper;
    
    /**
     * 查询问卷记录列表
     * 
     * @param record 问卷记录
     * @return 问卷记录集合
     */
    @Override
    public List<QuestionnaireRecordVO> selectRecordList(GcQuestionnaireRecord record) {
        List<QuestionnaireRecordVO> list = recordMapper.selectRecordList(record);
        
        // 对身份证号进行脱敏（可根据当前用户角色决定脱敏级别）
        // TODO: 获取当前用户角色
        String roleKey = "default"; 
        
        for (QuestionnaireRecordVO vo : list) {
            vo.setIdCardNo(IdCardDesensitizer.desensitize(vo.getIdCardNo(), roleKey));
        }
        
        return list;
    }
    
    /**
     * 查询问卷记录详情
     * 
     * @param recordId 记录ID
     * @return 问卷记录
     */
    @Override
    public GcQuestionnaireRecord selectRecordById(Long recordId) {
        return recordMapper.selectRecordById(recordId);
    }
    
    /**
     * 提交问卷（核心方法）
     * 包含：计算总分、判定重点人群、更新居民表、保存问卷记录
     * 
     * @param record 问卷记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitQuestionnaire(GcQuestionnaireRecord record) {
        // 1. 验证居民是否存在
        GcResident resident = residentMapper.selectResidentByIdCardNo(null);
        if (record.getResidentId() == null) {
            throw new ServiceException("居民ID不能为空");
        }
        
        // 通过ID查询居民
        // TODO: 添加通过ID查询的方法，或者使用现有的selectResidentById
        
        // 2. 验证问卷模板是否存在
        GcQuestionnaireTemplate template = templateMapper.selectTemplateById(record.getTemplateId());
        if (template == null) {
            throw new ServiceException("问卷模板不存在");
        }
        
        // 3. 检查模板是否启用
        if (template.getIsActive() == null || template.getIsActive() != 1) {
            throw new ServiceException("该问卷模板未启用");
        }
        
        // 4. 检查居民是否已填写该问卷
        GcQuestionnaireRecord existRecord = recordMapper.selectRecordByResidentAndTemplate(
            record.getResidentId(), record.getTemplateId());
        if (existRecord != null) {
            throw new ServiceException("该居民已填写过此问卷，不允许重复提交");
        }
        
        // 5. 验证答案内容不为空
        if (record.getAnswerContent() == null || record.getAnswerContent().isEmpty()) {
            throw new ServiceException("问卷答案不能为空");
        }
        
        // 6. 计算问卷总分
        int totalScore = 0;
        try {
            totalScore = QuestionnaireScoreCalculator.calculateScore(
                template.getTemplateContent(), 
                record.getAnswerContent()
            );
        } catch (Exception e) {
            throw new ServiceException("问卷评分计算失败: " + e.getMessage());
        }
        
        // 7. 判定是否为重点人群
        Integer isFocusGroup = QuestionnaireScoreCalculator.judgeFocusGroup(
            totalScore, 
            template.getFocusGroupThreshold()
        );
        
        // 8. 设置问卷记录的总分和重点人群标识
        record.setTotalScore(totalScore);
        record.setIsFocusGroup(isFocusGroup);
        
        // 9. 设置填写时间
        if (record.getFillTime() == null) {
            record.setFillTime(new Date());
        }
        
        // 10. 保存问卷记录
        int result = recordMapper.insertRecord(record);
        
        // 11. 更新居民表的重点人群标识
        if (result > 0) {
            residentMapper.updateResidentFocusGroup(record.getResidentId(), isFocusGroup);
        }
        
        return result;
    }
    
    /**
     * 修改问卷记录
     * 
     * @param record 问卷记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecord(GcQuestionnaireRecord record) {
        // 1. 验证问卷记录是否存在
        GcQuestionnaireRecord existRecord = recordMapper.selectRecordById(record.getRecordId());
        if (existRecord == null) {
            throw new ServiceException("问卷记录不存在");
        }
        
        // 2. 如果修改了答案内容，需要重新计算总分和重点人群标识
        if (record.getAnswerContent() != null && 
            !record.getAnswerContent().equals(existRecord.getAnswerContent())) {
            
            // 获取问卷模板
            GcQuestionnaireTemplate template = templateMapper.selectTemplateById(existRecord.getTemplateId());
            if (template == null) {
                throw new ServiceException("问卷模板不存在");
            }
            
            // 重新计算总分
            int totalScore = QuestionnaireScoreCalculator.calculateScore(
                template.getTemplateContent(), 
                record.getAnswerContent()
            );
            
            // 重新判定重点人群
            Integer isFocusGroup = QuestionnaireScoreCalculator.judgeFocusGroup(
                totalScore, 
                template.getFocusGroupThreshold()
            );
            
            // 设置新的总分和重点人群标识
            record.setTotalScore(totalScore);
            record.setIsFocusGroup(isFocusGroup);
            
            // 更新居民表的重点人群标识
            residentMapper.updateResidentFocusGroup(existRecord.getResidentId(), isFocusGroup);
        }
        
        // 3. 更新问卷记录
        return recordMapper.updateRecord(record);
    }
    
    /**
     * 删除问卷记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRecordById(Long recordId) {
        // 获取问卷记录信息（用于删除后重新判定居民是否为重点人群）
        GcQuestionnaireRecord record = recordMapper.selectRecordById(recordId);
        if (record == null) {
            throw new ServiceException("问卷记录不存在");
        }
        
        // 删除问卷记录
        int result = recordMapper.deleteRecordById(recordId);
        
        // 删除后，需要重新判定该居民是否为重点人群
        // 查询该居民是否还有其他问卷记录且为重点人群
        if (result > 0) {
            // TODO: 这里可以优化为查询该居民所有问卷记录，重新判定是否为重点人群
            // 简化处理：如果删除的记录标识为重点人群，则将居民标识设为非重点人群
            if (record.getIsFocusGroup() == 1) {
                residentMapper.updateResidentFocusGroup(record.getResidentId(), 0);
            }
        }
        
        return result;
    }
    
    /**
     * 批量删除问卷记录
     * 
     * @param recordIds 记录ID数组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRecordByIds(Long[] recordIds) {
        // 获取所有要删除的记录信息
        for (Long recordId : recordIds) {
            GcQuestionnaireRecord record = recordMapper.selectRecordById(recordId);
            if (record != null && record.getIsFocusGroup() == 1) {
                // 简化处理：如果删除的记录标识为重点人群，则将居民标识设为非重点人群
                residentMapper.updateResidentFocusGroup(record.getResidentId(), 0);
            }
        }
        
        return recordMapper.deleteRecordByIds(recordIds);
    }
}
