package com.javaxiaobear.module.gc.mapper;

import java.util.List;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.vo.QuestionnaireRecordVO;

/**
 * 问卷记录Mapper接口
 * 
 * @author javaxiaobear
 */
public interface GcQuestionnaireRecordMapper {
    
    /**
     * 查询问卷记录列表
     * 
     * @param record 问卷记录
     * @return 问卷记录集合
     */
    List<QuestionnaireRecordVO> selectRecordList(GcQuestionnaireRecord record);
    
    /**
     * 根据记录ID查询问卷记录
     * 
     * @param recordId 记录ID
     * @return 问卷记录
     */
    GcQuestionnaireRecord selectRecordById(Long recordId);
    
    /**
     * 根据居民ID查询问卷记录
     * 
     * @param residentId 居民ID
     * @return 问卷记录
     */
    GcQuestionnaireRecord selectRecordByResidentId(Long residentId);
    
    /**
     * 新增问卷记录
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
     * 删除问卷记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    int deleteRecordById(Long recordId);
}
