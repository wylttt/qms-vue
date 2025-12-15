-- =========================================
-- 濂溪区胃癌筛查信息系统 - 第三阶段数据字典
-- =========================================

-- 预约状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (106, '预约状态', 'gc_appointment_status', '0', 'admin', NOW(), '采血预约状态字典')
ON DUPLICATE KEY UPDATE dict_name = '预约状态';

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(118, 1, '已预约', 'pending', 'gc_appointment_status', '0', 'admin', NOW()),
(119, 2, '已取消', 'cancelled', 'gc_appointment_status', '0', 'admin', NOW()),
(120, 3, '已完成', 'completed', 'gc_appointment_status', '0', 'admin', NOW())
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label);

-- 推送状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (107, '推送状态', 'gc_push_status', '0', 'admin', NOW(), '推送状态字典')
ON DUPLICATE KEY UPDATE dict_name = '推送状态';

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(121, 1, '待推送', 'pending', 'gc_push_status', '0', 'admin', NOW()),
(122, 2, '推送中', 'pushing', 'gc_push_status', '0', 'admin', NOW()),
(123, 3, '已推送', 'pushed', 'gc_push_status', '0', 'admin', NOW()),
(124, 4, '推送失败', 'failed', 'gc_push_status', '0', 'admin', NOW())
ON DUPLICATE KEY UPDATE dict_label = VALUES(dict_label);
