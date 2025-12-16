-- 濂溪区胃癌筛查信息系统 - 第四阶段数据字典
-- 筛查结果与随访功能模块

-- 筛查结果类型字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('筛查结果类型', 'gc_screening_result_type', '0', '血液筛查结果分类', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '低风险', 'low_risk', 'gc_screening_result_type', '0', NOW()),
(2, '高风险', 'high_risk', 'gc_screening_result_type', '0', NOW());

-- 随访类型字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('随访类型', 'gc_follow_up_type', '0', '随访对象类型', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '血液筛查高风险', 'blood_high_risk', 'gc_follow_up_type', '0', NOW()),
(2, '胃镜检查异常', 'gastroscopy_abnormal', 'gc_follow_up_type', '0', NOW()),
(3, '其他原因', 'other', 'gc_follow_up_type', '0', NOW());

-- 随访状态字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('随访状态', 'gc_follow_up_status', '0', '随访对象状态', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '待随访', 'pending', 'gc_follow_up_status', '0', NOW()),
(2, '已分配', 'assigned', 'gc_follow_up_status', '0', NOW()),
(3, '随访中', 'in_progress', 'gc_follow_up_status', '0', NOW()),
(4, '已完成', 'completed', 'gc_follow_up_status', '0', NOW());

-- 随访优先级字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('随访优先级', 'gc_follow_up_level', '0', '随访优先级分类', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '高', 'high', 'gc_follow_up_level', '0', NOW()),
(2, '中', 'medium', 'gc_follow_up_level', '0', NOW()),
(3, '低', 'low', 'gc_follow_up_level', '0', NOW());

-- 跟踪方式字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('跟踪方式', 'gc_track_type', '0', '随访跟踪方式', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '电话', 'phone', 'gc_track_type', '0', NOW()),
(2, '短信', 'sms', 'gc_track_type', '0', NOW()),
(3, '上门', 'visit', 'gc_track_type', '0', NOW()),
(4, '其他', 'other', 'gc_track_type', '0', NOW());

-- 跟踪结果字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time) 
VALUES ('跟踪结果', 'gc_track_result', '0', '随访跟踪结果', NOW());

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) VALUES
(1, '成功联系', 'success', 'gc_track_result', '0', NOW()),
(2, '未联系上', 'failed', 'gc_track_result', '0', NOW()),
(3, '拒绝', 'refused', 'gc_track_result', '0', NOW());
