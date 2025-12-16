/*
 胃癌筛查问卷模板示例数据
 
 说明:
 1. 本文件提供了濂溪区胃癌筛查问卷模板的示例数据
 2. 问卷内容以JSON格式存储,支持动态渲染
 3. 重点人群判定基于风险评分,达到阈值则标记为重点人群
*/

SET NAMES utf8mb4;

-- ----------------------------
-- 插入胃癌筛查问卷模板
-- ----------------------------
INSERT INTO gc_questionnaire_template (
  template_id,
  template_name,
  template_code,
  version,
  description,
  template_content,
  focus_threshold,
  status,
  create_by,
  create_time
) VALUES (
  1,
  '濂溪区胃癌筛查调查问卷',
  'GC_LIANXI_V1',
  'V1.0',
  '濂溪区胃癌早期筛查调查问卷,用于评估居民胃癌风险等级',
  '{
    "title": "濂溪区胃癌筛查调查问卷",
    "version": "V1.0",
    "sections": [
      {
        "sectionId": "section_1",
        "sectionTitle": "基本信息",
        "questions": [
          {
            "questionId": "q1",
            "questionText": "您的年龄范围",
            "questionType": "radio",
            "required": true,
            "riskScore": 0,
            "options": [
              {"optionId": "q1_1", "optionText": "40岁以下", "score": 0},
              {"optionId": "q1_2", "optionText": "40-49岁", "score": 5},
              {"optionId": "q1_3", "optionText": "50-59岁", "score": 10},
              {"optionId": "q1_4", "optionText": "60-69岁", "score": 15},
              {"optionId": "q1_5", "optionText": "70岁及以上", "score": 20}
            ]
          }
        ]
      },
      {
        "sectionId": "section_2",
        "sectionTitle": "既往病史",
        "questions": [
          {
            "questionId": "q2",
            "questionText": "您是否患有胃溃疡?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q2_1", "optionText": "否", "score": 0},
              {"optionId": "q2_2", "optionText": "是", "score": 15}
            ]
          },
          {
            "questionId": "q3",
            "questionText": "您是否患有萎缩性胃炎?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q3_1", "optionText": "否", "score": 0},
              {"optionId": "q3_2", "optionText": "是", "score": 20}
            ]
          },
          {
            "questionId": "q4",
            "questionText": "您是否患有胃息肉?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q4_1", "optionText": "否", "score": 0},
              {"optionId": "q4_2", "optionText": "是", "score": 10}
            ]
          },
          {
            "questionId": "q5",
            "questionText": "您是否有幽门螺杆菌感染史?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q5_1", "optionText": "否", "score": 0},
              {"optionId": "q5_2", "optionText": "不确定", "score": 5},
              {"optionId": "q5_3", "optionText": "是", "score": 15}
            ]
          }
        ]
      },
      {
        "sectionId": "section_3",
        "sectionTitle": "家族史",
        "questions": [
          {
            "questionId": "q6",
            "questionText": "您的直系亲属(父母、兄弟姐妹、子女)中是否有人患胃癌?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q6_1", "optionText": "否", "score": 0},
              {"optionId": "q6_2", "optionText": "是", "score": 25}
            ]
          },
          {
            "questionId": "q7",
            "questionText": "您的直系亲属中是否有人患其他消化系统肿瘤(如食管癌、结肠癌)?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q7_1", "optionText": "否", "score": 0},
              {"optionId": "q7_2", "optionText": "是", "score": 10}
            ]
          }
        ]
      },
      {
        "sectionId": "section_4",
        "sectionTitle": "生活习惯",
        "questions": [
          {
            "questionId": "q8",
            "questionText": "您是否吸烟?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q8_1", "optionText": "从不吸烟", "score": 0},
              {"optionId": "q8_2", "optionText": "已戒烟", "score": 3},
              {"optionId": "q8_3", "optionText": "偶尔吸烟", "score": 5},
              {"optionId": "q8_4", "optionText": "经常吸烟", "score": 10}
            ]
          },
          {
            "questionId": "q9",
            "questionText": "您是否饮酒?",
            "questionType": "radio",
            "required": true,
            "options": [
              {"optionId": "q9_1", "optionText": "从不饮酒", "score": 0},
              {"optionId": "q9_2", "optionText": "偶尔饮酒", "score": 3},
              {"optionId": "q9_3", "optionText": "经常饮酒", "score": 8}
            ]
          },
          {
            "questionId": "q10",
            "questionText": "您的饮食习惯(可多选)",
            "questionType": "checkbox",
            "required": true,
            "options": [
              {"optionId": "q10_1", "optionText": "经常食用腌制食品", "score": 8},
              {"optionId": "q10_2", "optionText": "经常食用烧烤、油炸食品", "score": 5},
              {"optionId": "q10_3", "optionText": "饮食偏咸", "score": 5},
              {"optionId": "q10_4", "optionText": "饮食不规律", "score": 5},
              {"optionId": "q10_5", "optionText": "饮食清淡、规律", "score": 0}
            ]
          }
        ]
      },
      {
        "sectionId": "section_5",
        "sectionTitle": "症状评估",
        "questions": [
          {
            "questionId": "q11",
            "questionText": "近期您是否出现以下症状(可多选)",
            "questionType": "checkbox",
            "required": true,
            "options": [
              {"optionId": "q11_1", "optionText": "无明显症状", "score": 0},
              {"optionId": "q11_2", "optionText": "上腹部不适或疼痛", "score": 10},
              {"optionId": "q11_3", "optionText": "消化不良、食欲减退", "score": 8},
              {"optionId": "q11_4", "optionText": "反酸、烧心", "score": 5},
              {"optionId": "q11_5", "optionText": "黑便或大便潜血", "score": 15},
              {"optionId": "q11_6", "optionText": "不明原因体重下降", "score": 15},
              {"optionId": "q11_7", "optionText": "吞咽困难", "score": 12}
            ]
          },
          {
            "questionId": "q12",
            "questionText": "以上症状持续时间",
            "questionType": "radio",
            "required": false,
            "displayCondition": "q11 != q11_1",
            "options": [
              {"optionId": "q12_1", "optionText": "1个月以内", "score": 5},
              {"optionId": "q12_2", "optionText": "1-3个月", "score": 10},
              {"optionId": "q12_3", "optionText": "3个月以上", "score": 15}
            ]
          }
        ]
      }
    ],
    "scoringRules": {
      "totalScoreCalculation": "sum",
      "focusGroupThreshold": 60,
      "riskLevels": [
        {"level": "低风险", "minScore": 0, "maxScore": 39},
        {"level": "中风险", "minScore": 40, "maxScore": 59},
        {"level": "高风险", "minScore": 60, "maxScore": 999}
      ]
    }
  }',
  60,
  '1',
  'admin',
  NOW()
) ON DUPLICATE KEY UPDATE template_name = VALUES(template_name);

-- ----------------------------
-- 查询验证模板数据
-- ----------------------------
SELECT 
  template_id,
  template_name,
  template_code,
  version,
  focus_threshold,
  CASE status
    WHEN '0' THEN '草稿'
    WHEN '1' THEN '启用'
    WHEN '2' THEN '停用'
  END AS status_text,
  create_by,
  create_time
FROM gc_questionnaire_template
WHERE template_code = 'GC_LIANXI_V1';

/*
 问卷模板设计说明:
 
 1. 问卷结构
    - sections: 问卷分节,包含基本信息、既往病史、家族史、生活习惯、症状评估等
    - questions: 每个section包含多个问题
    - questionType: 问题类型(radio单选/checkbox多选/text文本/date日期)
    
 2. 评分规则
    - 每个选项都有对应的score值
    - 总分计算方式: sum(所有选中选项的score)
    - 重点人群判定: 总分 >= focus_threshold (默认60分)
    
 3. 风险等级
    - 低风险: 0-39分
    - 中风险: 40-59分
    - 高风险: 60分以上(重点人群)
    
 4. 扩展性
    - 支持添加新的section和question
    - 支持修改评分规则和阈值
    - 支持版本管理,便于A/B测试和迭代优化
    
 5. 显示条件
    - displayCondition: 条件显示逻辑,根据前置问题答案决定是否显示当前问题
    - 例如: "displayCondition": "q11 != q11_1" 表示只有q11不选择"无明显症状"时才显示
*/
