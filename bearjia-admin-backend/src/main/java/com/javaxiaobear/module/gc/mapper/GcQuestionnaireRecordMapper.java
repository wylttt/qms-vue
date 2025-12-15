package com.javaxiaobear.module.gc.mapper;

import com.javaxiaobear.module.gc.domain.GcQuestionnaireRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问卷记录Mapper接口
 * 
 * @author Bear
 * @date 2025-01-09
 */
@Mapper
public interface GcQuestionnaireRecordMapper {
    
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

    /**
     * 批量删除问卷记录
     * 
     * @param recordIds 需要删除的记录ID数组
     * @return 结果
     */
    int deleteRecordByIds(Long[] recordIds);

    /**
     * 统计问卷完成数量
     * 
     * @param record 查询条件
     * @return 完成数量
     */
    int countRecord(GcQuestionnaireRecord record);

    /**
     * 根据调查员统计问卷数量
     * 
     * @param surveyorId 调查员ID
     * @return 问卷数量
     */
    int countRecordBySurveyorId(@Param("surveyorId") Long surveyorId);
}
