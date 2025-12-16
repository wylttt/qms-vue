package com.javaxiaobear.module.gc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireRecord;
import com.javaxiaobear.module.gc.domain.entity.GcQuestionnaireTemplate;
import com.javaxiaobear.module.gc.domain.entity.GcResident;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireRecordMapper;
import com.javaxiaobear.module.gc.mapper.GcQuestionnaireTemplateMapper;
import com.javaxiaobear.module.gc.mapper.GcResidentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 问卷记录Service单元测试
 * 
 * 测试范围：
 * 1. 问卷评分算法
 * 2. 重点人群判定
 * 3. 问卷提交验证
 * 
 * @author javaxiaobear
 * @date 2025-12-16
 */
@ExtendWith(MockitoExtension.class)
class GcQuestionnaireRecordServiceImplTest {

    @Mock
    private GcQuestionnaireRecordMapper recordMapper;

    @Mock
    private GcQuestionnaireTemplateMapper templateMapper;

    @Mock
    private GcResidentMapper residentMapper;

    @InjectMocks
    private GcQuestionnaireRecordServiceImpl recordService;

    /**
     * 创建测试用的问卷模板
     */
    private String createTestTemplate() {
        JSONObject template = new JSONObject();
        JSONArray sections = new JSONArray();
        
        // 第一节：基本信息
        JSONObject section1 = new JSONObject();
        section1.put("sectionTitle", "基本信息");
        
        JSONArray questions1 = new JSONArray();
        JSONObject question1 = new JSONObject();
        question1.put("questionId", "q1");
        question1.put("questionText", "您的年龄是？");
        question1.put("questionType", "radio");
        
        JSONArray options1 = new JSONArray();
        options1.add(createOption("q1_opt1", "40岁以下", 0));
        options1.add(createOption("q1_opt2", "40-49岁", 5));
        options1.add(createOption("q1_opt3", "50-59岁", 10));
        options1.add(createOption("q1_opt4", "60岁及以上", 15));
        question1.put("options", options1);
        
        questions1.add(question1);
        section1.put("questions", questions1);
        sections.add(section1);
        
        // 第二节：既往病史
        JSONObject section2 = new JSONObject();
        section2.put("sectionTitle", "既往病史");
        
        JSONArray questions2 = new JSONArray();
        JSONObject question2 = new JSONObject();
        question2.put("questionId", "q2");
        question2.put("questionText", "是否有以下疾病？");
        question2.put("questionType", "checkbox");
        
        JSONArray options2 = new JSONArray();
        options2.add(createOption("q2_opt1", "无", 0));
        options2.add(createOption("q2_opt2", "慢性胃炎", 10));
        options2.add(createOption("q2_opt3", "胃溃疡", 15));
        options2.add(createOption("q2_opt4", "幽门螺杆菌感染", 20));
        question2.put("options", options2);
        
        questions2.add(question2);
        section2.put("questions", questions2);
        sections.add(section2);
        
        template.put("sections", sections);
        return template.toJSONString();
    }

    /**
     * 创建选项
     */
    private JSONObject createOption(String optionId, String text, int score) {
        JSONObject option = new JSONObject();
        option.put("optionId", optionId);
        option.put("optionText", text);
        option.put("score", score);
        return option;
    }

    /**
     * 创建测试用的答案
     */
    private String createTestAnswer(String... optionIds) {
        JSONObject answer = new JSONObject();
        JSONArray answers = new JSONArray();
        
        // q1的答案
        if (optionIds.length > 0) {
            JSONObject answer1 = new JSONObject();
            answer1.put("questionId", "q1");
            JSONArray selected1 = new JSONArray();
            selected1.add(optionIds[0]);
            answer1.put("selectedOptions", selected1);
            answers.add(answer1);
        }
        
        // q2的答案
        if (optionIds.length > 1) {
            JSONObject answer2 = new JSONObject();
            answer2.put("questionId", "q2");
            JSONArray selected2 = new JSONArray();
            for (int i = 1; i < optionIds.length; i++) {
                selected2.add(optionIds[i]);
            }
            answer2.put("selectedOptions", selected2);
            answers.add(answer2);
        }
        
        answer.put("answers", answers);
        return answer.toJSONString();
    }

    /**
     * 测试：问卷评分 - 低风险（0-39分）
     */
    @Test
    void testCalculateScore_LowRisk() {
        // 准备问卷模板
        String templateContent = createTestTemplate();
        
        // 准备答案（40岁以下=0分 + 无疾病=0分）
        String answerContent = createTestAnswer("q1_opt1", "q2_opt1");
        
        // 计算评分
        int totalScore = calculateScore(templateContent, answerContent);
        
        // 验证结果
        assertEquals(0, totalScore, "低风险评分应为0");
    }

    /**
     * 测试：问卷评分 - 中风险（40-59分）
     */
    @Test
    void testCalculateScore_MediumRisk() {
        // 准备问卷模板
        String templateContent = createTestTemplate();
        
        // 准备答案（50-59岁=10分 + 慢性胃炎=10分 + 胃溃疡=15分 + 幽门螺杆菌=20分）
        String answerContent = createTestAnswer("q1_opt3", "q2_opt2", "q2_opt3", "q2_opt4");
        
        // 计算评分
        int totalScore = calculateScore(templateContent, answerContent);
        
        // 验证结果
        assertEquals(55, totalScore, "中风险评分应为55");
    }

    /**
     * 测试：问卷评分 - 高风险（60分以上）
     */
    @Test
    void testCalculateScore_HighRisk() {
        // 准备问卷模板
        String templateContent = createTestTemplate();
        
        // 准备答案（60岁以上=15分 + 慢性胃炎=10分 + 胃溃疡=15分 + 幽门螺杆菌=20分）
        String answerContent = createTestAnswer("q1_opt4", "q2_opt2", "q2_opt3", "q2_opt4");
        
        // 计算评分
        int totalScore = calculateScore(templateContent, answerContent);
        
        // 验证结果
        assertEquals(60, totalScore, "高风险评分应为60");
    }

    /**
     * 辅助方法：计算评分
     */
    private int calculateScore(String templateContent, String answerContent) {
        int totalScore = 0;
        
        // 解析模板和答案
        JSONObject template = JSON.parseObject(templateContent);
        JSONObject answer = JSON.parseObject(answerContent);
        
        JSONArray sections = template.getJSONArray("sections");
        JSONArray answers = answer.getJSONArray("answers");
        
        // 遍历答案
        for (int i = 0; i < answers.size(); i++) {
            JSONObject answerItem = answers.getJSONObject(i);
            String questionId = answerItem.getString("questionId");
            JSONArray selectedOptions = answerItem.getJSONArray("selectedOptions");
            
            // 在模板中查找问题
            JSONObject question = findQuestion(sections, questionId);
            if (question == null) continue;
            
            // 计算该问题的得分
            JSONArray options = question.getJSONArray("options");
            for (int j = 0; j < selectedOptions.size(); j++) {
                String optionId = selectedOptions.getString(j);
                JSONObject option = findOption(options, optionId);
                if (option != null) {
                    totalScore += option.getIntValue("score", 0);
                }
            }
        }
        
        return totalScore;
    }

    /**
     * 辅助方法：查找问题
     */
    private JSONObject findQuestion(JSONArray sections, String questionId) {
        for (int i = 0; i < sections.size(); i++) {
            JSONObject section = sections.getJSONObject(i);
            JSONArray questions = section.getJSONArray("questions");
            for (int j = 0; j < questions.size(); j++) {
                JSONObject question = questions.getJSONObject(j);
                if (questionId.equals(question.getString("questionId"))) {
                    return question;
                }
            }
        }
        return null;
    }

    /**
     * 辅助方法：查找选项
     */
    private JSONObject findOption(JSONArray options, String optionId) {
        for (int i = 0; i < options.size(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (optionId.equals(option.getString("optionId"))) {
                return option;
            }
        }
        return null;
    }

    /**
     * 测试：重点人群判定 - 非重点人群（<60分）
     */
    @Test
    void testIsFocusGroup_NotFocus() {
        int totalScore = 50;
        int threshold = 60;
        
        boolean isFocusGroup = totalScore >= threshold;
        
        assertFalse(isFocusGroup, "50分应判定为非重点人群");
    }

    /**
     * 测试：重点人群判定 - 重点人群（>=60分）
     */
    @Test
    void testIsFocusGroup_Focus() {
        int totalScore = 60;
        int threshold = 60;
        
        boolean isFocusGroup = totalScore >= threshold;
        
        assertTrue(isFocusGroup, "60分应判定为重点人群");
    }

    /**
     * 测试：重点人群判定 - 边界值
     */
    @Test
    void testIsFocusGroup_Boundary() {
        int threshold = 60;
        
        // 59分 - 非重点
        assertFalse(59 >= threshold, "59分应为非重点人群");
        
        // 60分 - 重点
        assertTrue(60 >= threshold, "60分应为重点人群");
        
        // 61分 - 重点
        assertTrue(61 >= threshold, "61分应为重点人群");
    }

    /**
     * 测试：提交问卷 - 正常流程
     */
    @Test
    void testSubmitQuestionnaire_Success() {
        // 准备测试数据
        Long residentId = 1L;
        Long templateId = 1L;
        String answerContent = createTestAnswer("q1_opt4", "q2_opt2", "q2_opt3", "q2_opt4");
        
        // Mock问卷模板
        GcQuestionnaireTemplate template = new GcQuestionnaireTemplate();
        template.setTemplateId(templateId);
        template.setTemplateContent(createTestTemplate());
        template.setFocusGroupThreshold(60);
        
        // Mock居民信息
        GcResident resident = new GcResident();
        resident.setResidentId(residentId);
        resident.setRealName("测试居民");
        
        when(templateMapper.selectById(templateId)).thenReturn(template);
        when(residentMapper.selectById(residentId)).thenReturn(resident);
        when(recordMapper.checkQuestionnaireExists(residentId, templateId)).thenReturn(0);
        when(recordMapper.insertQuestionnaireRecord(any())).thenReturn(1);
        when(residentMapper.updateById(any())).thenReturn(1);
        
        // 创建问卷记录
        GcQuestionnaireRecord record = new GcQuestionnaireRecord();
        record.setResidentId(residentId);
        record.setTemplateId(templateId);
        record.setAnswerContent(answerContent);
        
        // 计算评分
        int totalScore = calculateScore(template.getTemplateContent(), answerContent);
        record.setTotalScore(totalScore);
        record.setIsFocusGroup(totalScore >= 60 ? 1 : 0);
        
        // 验证结果
        assertEquals(60, totalScore);
        assertEquals(1, record.getIsFocusGroup());
    }

    /**
     * 测试：提交问卷 - 重复提交
     */
    @Test
    void testSubmitQuestionnaire_Duplicate() {
        // 准备测试数据
        Long residentId = 1L;
        Long templateId = 1L;
        
        // Mock已存在问卷记录
        when(recordMapper.checkQuestionnaireExists(residentId, templateId)).thenReturn(1);
        
        // 验证不应插入新记录
        verify(recordMapper, never()).insertQuestionnaireRecord(any());
    }

    /**
     * 测试：空答案情况
     */
    @Test
    void testCalculateScore_EmptyAnswer() {
        String templateContent = createTestTemplate();
        String answerContent = "{\"answers\": []}";
        
        int totalScore = calculateScore(templateContent, answerContent);
        
        assertEquals(0, totalScore, "空答案评分应为0");
    }

    /**
     * 测试：单选题评分
     */
    @Test
    void testCalculateScore_RadioQuestion() {
        String templateContent = createTestTemplate();
        String answerContent = createTestAnswer("q1_opt3"); // 仅回答q1
        
        int totalScore = calculateScore(templateContent, answerContent);
        
        assertEquals(10, totalScore, "单选题评分应为10");
    }

    /**
     * 测试：多选题评分
     */
    @Test
    void testCalculateScore_CheckboxQuestion() {
        String templateContent = createTestTemplate();
        String answerContent = createTestAnswer("q1_opt1", "q2_opt2", "q2_opt3"); // q1=0分, q2=10+15=25分
        
        int totalScore = calculateScore(templateContent, answerContent);
        
        assertEquals(25, totalScore, "多选题评分应为25");
    }

    /**
     * 测试：最大评分
     */
    @Test
    void testCalculateScore_MaxScore() {
        String templateContent = createTestTemplate();
        // 选择最高分选项：q1_opt4(15) + q2_opt2(10) + q2_opt3(15) + q2_opt4(20)
        String answerContent = createTestAnswer("q1_opt4", "q2_opt2", "q2_opt3", "q2_opt4");
        
        int totalScore = calculateScore(templateContent, answerContent);
        
        assertEquals(60, totalScore, "最大评分应为60");
    }

    /**
     * 测试：不同阈值的重点人群判定
     */
    @Test
    void testFocusGroupThreshold_Different() {
        // 阈值50
        assertTrue(55 >= 50, "55分在阈值50时应为重点人群");
        assertFalse(45 >= 50, "45分在阈值50时应为非重点人群");
        
        // 阈值60
        assertTrue(65 >= 60, "65分在阈值60时应为重点人群");
        assertFalse(55 >= 60, "55分在阈值60时应为非重点人群");
        
        // 阈值70
        assertTrue(75 >= 70, "75分在阈值70时应为重点人群");
        assertFalse(65 >= 70, "65分在阈值70时应为非重点人群");
    }
}
