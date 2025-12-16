package com.javaxiaobear.module.gc.service;

import com.javaxiaobear.module.gc.domain.GcQuestionnaireRecord;

import java.util.List;

/**
 * 问卷记录Service接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
public interface IGcQuestionnaireRecordService {
    
    /**
     * 查询问卷记录列表
     * 
     * @param record 问卷记录
     * @return 问卷记录集合
     */
    List<GcQuestionnaireRecord> selectRecordList(GcQuestionnaireRecord record);

    /**
     * 查询问卷记录详情
     * 
     * @param recordId 记录ID
     * @return 问卷记录
     */
    GcQuestionnaireRecord selectRecordById(Long recordId);

    /**
     * 新增问卷记录(自动计算风险评分和判定重点人群)
     * 
     * @param record 问卷记录
     * @return 结果
     */
    int insertRecord(GcQuestionnaireRecord record);

    /**
     * 修改问卷记录
     * 
     * @param record 问卷记录
     * @return 结果
     */
    int updateRecord(GcQuestionnaireRecord record);

    /**
     * 批量删除问卷记录
     * 
     * @param recordIds 需要删除的记录ID数组
     * @return 结果
     */
    int deleteRecordByIds(Long[] recordIds);

    /**
     * 删除问卷记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    int deleteRecordById(Long recordId);

    /**
     * 统计问卷完成数量
     * 
     * @param record 查询条件
     * @return 完成数量
     */
    int countRecord(GcQuestionnaireRecord record);

    /**
     * 根据问卷答案计算风险评分
     * 
     * @param answers 问卷答案JSON
     * @param templateContent 问卷模板JSON
     * @return 风险评分
     */
    int calculateRiskScore(String answers, String templateContent);

    /**
     * 判断是否为重点人群
     * 
     * @param riskScore 风险评分
     * @param focusThreshold 重点人群阈值
     * @return 是否重点人群
     */
    boolean isFocusGroup(int riskScore, int focusThreshold);
}
