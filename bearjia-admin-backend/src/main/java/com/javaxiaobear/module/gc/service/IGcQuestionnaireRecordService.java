package com.javaxiaobear.module.gc.service;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.vo.QuestionnaireRecordVO;

/**
 * 问卷记录Service接口
 * 
 * @author javaxiaobear
 */
public interface IGcQuestionnaireRecordService {
    
    /**
     * 查询问卷记录列表
     * 
     * @param record 问卷记录
     * @return 问卷记录集合
     */
    List<QuestionnaireRecordVO> selectRecordList(GcQuestionnaireRecord record);
    
    /**
     * 查询问卷记录详情
     * 
     * @param recordId 记录ID
     * @return 问卷记录
     */
    GcQuestionnaireRecord selectRecordById(Long recordId);
    
    /**
     * 提交问卷（核心方法）
     * 包含：计算总分、判定重点人群、更新居民表、保存问卷记录
     * 
     * @param record 问卷记录（包含居民ID、模板ID、答案内容、协助填写调查员ID）
     * @return 结果
     */
    int submitQuestionnaire(GcQuestionnaireRecord record);
    
    /**
     * 修改问卷记录
     * 
     * @param record 问卷记录
     * @return 结果
     */
    int updateRecord(GcQuestionnaireRecord record);
    
    /**
     * 删除问卷记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    int deleteRecordById(Long recordId);
    
    /**
     * 批量删除问卷记录
     * 
     * @param recordIds 记录ID数组
     * @return 结果
     */
    int deleteRecordByIds(Long[] recordIds);
}
