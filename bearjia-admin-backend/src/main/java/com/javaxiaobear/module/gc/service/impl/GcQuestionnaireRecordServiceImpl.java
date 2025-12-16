package com.javaxiaobear.module.gc.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.module.gc.domain.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.domain.GcResident;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireRecordMapper;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireTemplateMapper;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import com.javaxiaobear.module.gc.service.IGcQuestionnaireRecordService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

/**
 * 问卷记录Service业务层处理
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Service
public class GcQuestionnaireRecordServiceImpl implements IGcQuestionnaireRecordService {
    
    @Autowired
    private GcQuestionnaireRecordMapper recordMapper;
    
    @Autowired
    private GcQuestionnaireTemplateMapper templateMapper;
    
    @Autowired
    private GcResidentMapper residentMapper;

    @Override
    public List<GcQuestionnaireRecord> selectRecordList(GcQuestionnaireRecord record) {
        return recordMapper.selectRecordList(record);
    }

    @Override
    public GcQuestionnaireRecord selectRecordById(Long recordId) {
        return recordMapper.selectRecordById(recordId);
    }

    @Override
    public int insertRecord(GcQuestionnaireRecord record) {
        // 验证居民是否已填写过问卷
        if (record.getResidentId() != null) {
            GcQuestionnaireRecord existRecord = recordMapper.selectRecordByResidentId(record.getResidentId());
            if (existRecord != null) {
                throw new RuntimeException("该居民已填写过问卷,不允许重复提交");
            }
        }
        
        // 获取问卷模板
        GcQuestionnaireTemplate template = templateMapper.selectTemplateById(record.getTemplateId());
        if (template == null) {
            throw new RuntimeException("问卷模板不存在");
        }
        
        // 计算风险评分
        int riskScore = calculateRiskScore(record.getAnswers(), template.getTemplateContent());
        record.setRiskScore(riskScore);
        
        // 判定是否重点人群
        boolean isFocus = isFocusGroup(riskScore, template.getFocusThreshold());
        record.setIsFocusGroup(isFocus ? 1 : 0);
        
        // 设置提交时间
        if (record.getSubmitTime() == null) {
            record.setSubmitTime(new Date());
        }
        
        // 默认创建来源为居民
        if (StringUtils.isEmpty(record.getCreateSource())) {
            record.setCreateSource("resident");
        }
        
        int result = recordMapper.insertRecord(record);
        
        // 同步更新居民表的is_focus_group字段
        if (record.getResidentId() != null) {
            GcResident resident = new GcResident();
            resident.setResidentId(record.getResidentId());
            resident.setIsFocusGroup(record.getIsFocusGroup());
            residentMapper.updateResident(resident);
        }
        
        return result;
    }

    @Override
    public int updateRecord(GcQuestionnaireRecord record) {
        // 获取问卷模板
        GcQuestionnaireRecord existRecord = recordMapper.selectRecordById(record.getRecordId());
        if (existRecord == null) {
            throw new RuntimeException("问卷记录不存在");
        }
        
        GcQuestionnaireTemplate template = templateMapper.selectTemplateById(existRecord.getTemplateId());
        if (template == null) {
            throw new RuntimeException("问卷模板不存在");
        }
        
        // 重新计算风险评分
        if (StringUtils.isNotEmpty(record.getAnswers())) {
            int riskScore = calculateRiskScore(record.getAnswers(), template.getTemplateContent());
            record.setRiskScore(riskScore);
            
            // 重新判定是否重点人群
            boolean isFocus = isFocusGroup(riskScore, template.getFocusThreshold());
            record.setIsFocusGroup(isFocus ? 1 : 0);
        }
        
        int result = recordMapper.updateRecord(record);
        
        // 同步更新居民表的is_focus_group字段
        if (record.getIsFocusGroup() != null && existRecord.getResidentId() != null) {
            GcResident resident = new GcResident();
            resident.setResidentId(existRecord.getResidentId());
            resident.setIsFocusGroup(record.getIsFocusGroup());
            residentMapper.updateResident(resident);
        }
        
        return result;
    }

    @Override
    public int deleteRecordByIds(Long[] recordIds) {
        return recordMapper.deleteRecordByIds(recordIds);
    }

    @Override
    public int deleteRecordById(Long recordId) {
        return recordMapper.deleteRecordById(recordId);
    }

    @Override
    public int countRecord(GcQuestionnaireRecord record) {
        return recordMapper.countRecord(record);
    }

    /**
     * 根据问卷答案计算风险评分
     * 
     * 算法说明:
     * 1. 解析问卷模板JSON,获取每个题目的配置(包括题目类型、选项分值等)
     * 2. 解析问卷答案JSON,获取用户选择的答案
     * 3. 根据答案匹配模板中的分值规则,累加计算总分
     * 4. 支持单选题、多选题、判断题等多种题型
     * 
     * @param answers 问卷答案JSON
     * @param templateContent 问卷模板JSON
     * @return 风险评分
     */
    @Override
    public int calculateRiskScore(String answers, String templateContent) {
        if (StringUtils.isEmpty(answers) || StringUtils.isEmpty(templateContent)) {
            return 0;
        }
        
        try {
            JSONObject answersObj = JSON.parseObject(answers);
            JSONObject templateObj = JSON.parseObject(templateContent);
            JSONArray questions = templateObj.getJSONArray("questions");
            
            int totalScore = 0;
            
            // 遍历所有题目
            for (int i = 0; i < questions.size(); i++) {
                JSONObject question = questions.getJSONObject(i);
                String questionId = question.getString("id");
                String questionType = question.getString("type");
                
                // 获取用户答案
                Object answerValue = answersObj.get(questionId);
                if (answerValue == null) {
                    continue;
                }
                
                // 根据题目类型计算分值
                if ("single".equals(questionType)) {
                    // 单选题: 获取选中选项的分值
                    String selectedOption = answerValue.toString();
                    JSONArray options = question.getJSONArray("options");
                    for (int j = 0; j < options.size(); j++) {
                        JSONObject option = options.getJSONObject(j);
                        if (selectedOption.equals(option.getString("value"))) {
                            totalScore += option.getIntValue("score", 0);
                            break;
                        }
                    }
                } else if ("multiple".equals(questionType)) {
                    // 多选题: 累加所有选中选项的分值
                    JSONArray selectedOptions = (JSONArray) answerValue;
                    JSONArray options = question.getJSONArray("options");
                    for (int j = 0; j < selectedOptions.size(); j++) {
                        String selected = selectedOptions.getString(j);
                        for (int k = 0; k < options.size(); k++) {
                            JSONObject option = options.getJSONObject(k);
                            if (selected.equals(option.getString("value"))) {
                                totalScore += option.getIntValue("score", 0);
                                break;
                            }
                        }
                    }
                } else if ("boolean".equals(questionType)) {
                    // 判断题: true/false对应不同分值
                    boolean boolValue = (Boolean) answerValue;
                    totalScore += boolValue ? question.getIntValue("trueScore", 0) : question.getIntValue("falseScore", 0);
                }
            }
            
            return totalScore;
        } catch (Exception e) {
            // 如果JSON解析失败,返回0分
            return 0;
        }
    }

    /**
     * 判断是否为重点人群
     * 
     * @param riskScore 风险评分
     * @param focusThreshold 重点人群阈值
     * @return 是否重点人群
     */
    @Override
    public boolean isFocusGroup(int riskScore, int focusThreshold) {
        return riskScore >= focusThreshold;
    }
}
