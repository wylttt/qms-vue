package com.javaxiaobear.module.gc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 问卷评分计算器
 * 
 * @author javaxiaobear
 */
public class QuestionnaireScoreCalculator {
    
    /**
     * 计算问卷总分
     * 
     * @param templateContent 问卷模板JSON内容
     * @param answerContent 问卷答案JSON内容
     * @return 总分
     */
    public static int calculateScore(String templateContent, String answerContent) {
        int totalScore = 0;
        
        try {
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
                if (question == null) {
                    continue;
                }
                
                // 累加选项分数
                JSONArray options = question.getJSONArray("options");
                for (int j = 0; j < selectedOptions.size(); j++) {
                    String optionId = selectedOptions.getString(j);
                    JSONObject option = findOption(options, optionId);
                    if (option != null) {
                        totalScore += option.getIntValue("score");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("问卷评分计算失败: " + e.getMessage(), e);
        }
        
        return totalScore;
    }
    
    /**
     * 判定是否为重点人群
     * 
     * @param totalScore 总分
     * @param threshold 阈值
     * @return 是否为重点人群（0否/1是）
     */
    public static Integer judgeFocusGroup(int totalScore, int threshold) {
        return totalScore >= threshold ? 1 : 0;
    }
    
    /**
     * 在模板中查找问题
     * 
     * @param sections 问卷分节数组
     * @param questionId 问题ID
     * @return 问题对象
     */
    private static JSONObject findQuestion(JSONArray sections, String questionId) {
        for (int i = 0; i < sections.size(); i++) {
            JSONObject section = sections.getJSONObject(i);
            JSONArray questions = section.getJSONArray("questions");
            if (questions == null) {
                continue;
            }
            
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
     * 在选项中查找指定选项
     * 
     * @param options 选项数组
     * @param optionId 选项ID
     * @return 选项对象
     */
    private static JSONObject findOption(JSONArray options, String optionId) {
        if (options == null) {
            return null;
        }
        
        for (int i = 0; i < options.size(); i++) {
            JSONObject option = options.getJSONObject(i);
            if (optionId.equals(option.getString("optionId"))) {
                return option;
            }
        }
        return null;
    }
}
