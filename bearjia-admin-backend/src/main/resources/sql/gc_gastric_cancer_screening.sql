/*
 濂溪区胃癌筛查信息系统 - 数据库初始化脚本
 
 Author: System
 Date: 2025-12-12
 Description: 胃癌早期筛查问卷调查系统核心业务表结构
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gc_region (行政区划表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_region`;
CREATE TABLE `gc_region` (
  `region_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '区域ID',
  `region_name` VARCHAR(100) NOT NULL COMMENT '区域名称',
  `region_code` VARCHAR(20) NOT NULL COMMENT '行政区划代码(12位国标代码)',
  `region_level` TINYINT NOT NULL COMMENT '区域层级(1省/2市/3区/4街道/5社区)',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级区域ID',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常/1停用)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`region_id`),
  UNIQUE KEY `uk_region_code` (`region_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_region_level` (`region_level`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='行政区划表';

-- ----------------------------
-- Table structure for gc_sampling_site (采血点表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_sampling_site`;
CREATE TABLE `gc_sampling_site` (
  `site_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '采血点ID',
  `site_name` VARCHAR(100) NOT NULL COMMENT '采血点名称',
  `site_code` VARCHAR(50) DEFAULT NULL COMMENT '采血点编码',
  `region_id` BIGINT NOT NULL COMMENT '所属区域ID(街道/乡镇)',
  `address` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `street_manager_id` BIGINT DEFAULT NULL COMMENT '街道/乡镇管理员ID',
  `street_manager_name` VARCHAR(50) DEFAULT NULL COMMENT '街道/乡镇管理员姓名',
  `street_manager_phone` VARCHAR(11) DEFAULT NULL COMMENT '街道/乡镇管理员联系方式',
  `site_manager_id` BIGINT DEFAULT NULL COMMENT '采血点管理员ID',
  `site_manager_name` VARCHAR(50) DEFAULT NULL COMMENT '采血点管理员姓名',
  `site_manager_phone` VARCHAR(11) DEFAULT NULL COMMENT '采血点管理员联系方式',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常/1停用)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`site_id`),
  KEY `idx_region_id` (`region_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采血点表';

-- ----------------------------
-- Table structure for gc_surveyor (问卷调查员表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_surveyor`;
CREATE TABLE `gc_surveyor` (
  `surveyor_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '调查员ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户表ID',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `gender` CHAR(1) NOT NULL COMMENT '性别(0男/1女)',
  `region_id` BIGINT NOT NULL COMMENT '所属街道/乡镇ID',
  `street_manager_id` BIGINT DEFAULT NULL COMMENT '街道/乡镇管理员ID',
  `street_manager_phone` VARCHAR(11) DEFAULT NULL COMMENT '街道/乡镇管理员联系方式',
  `phone_number` VARCHAR(11) NOT NULL COMMENT '调查员联系方式',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常/1停用)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`surveyor_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_region_id` (`region_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问卷调查员表';

-- ----------------------------
-- Table structure for gc_resident (居民信息表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_resident`;
CREATE TABLE `gc_resident` (
  `resident_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '居民ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `age` INT NOT NULL COMMENT '年龄',
  `gender` CHAR(1) NOT NULL COMMENT '性别(0男/1女)',
  `id_card_no` VARCHAR(18) NOT NULL COMMENT '身份证号',
  `address` VARCHAR(200) NOT NULL COMMENT '详细住址',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `street` VARCHAR(50) NOT NULL COMMENT '街道/乡镇',
  `community` VARCHAR(50) NOT NULL COMMENT '社区/村',
  `contact_phone` VARCHAR(11) NOT NULL COMMENT '联系方式',
  `is_focus_group` TINYINT NOT NULL DEFAULT 0 COMMENT '是否重点人群(0否/1是)',
  `surveyor_id` BIGINT DEFAULT NULL COMMENT '问卷调查员ID',
  `appointment_site_id` BIGINT DEFAULT NULL COMMENT '预约采样点ID',
  `appointment_time` DATETIME DEFAULT NULL COMMENT '预约采样时间',
  `sampling_status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '采样状态(0未采样/1已采样)',
  `create_source` VARCHAR(20) NOT NULL DEFAULT 'resident' COMMENT '创建来源(resident居民/surveyor调查员)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`resident_id`),
  UNIQUE KEY `uk_id_card_no` (`id_card_no`),
  KEY `idx_district` (`district`),
  KEY `idx_street` (`street`),
  KEY `idx_community` (`community`),
  KEY `idx_surveyor_id` (`surveyor_id`),
  KEY `idx_appointment_site_id` (`appointment_site_id`),
  KEY `idx_sampling_status` (`sampling_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='居民信息表';

-- ----------------------------
-- Table structure for gc_questionnaire_template (问卷模板表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_questionnaire_template`;
CREATE TABLE `gc_questionnaire_template` (
  `template_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码',
  `version` VARCHAR(20) NOT NULL COMMENT '版本号',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '模板说明',
  `template_content` TEXT NOT NULL COMMENT '问卷内容(JSON格式)',
  `focus_threshold` INT NOT NULL DEFAULT 60 COMMENT '重点人群判定阈值',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0草稿/1启用/2停用)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问卷模板表';

-- ----------------------------
-- Table structure for gc_questionnaire_record (问卷记录表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_questionnaire_record`;
CREATE TABLE `gc_questionnaire_record` (
  `record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问卷记录ID',
  `resident_id` BIGINT NOT NULL COMMENT '居民ID',
  `template_id` BIGINT NOT NULL COMMENT '模板ID',
  `surveyor_id` BIGINT DEFAULT NULL COMMENT '调查员ID(协助填写时)',
  `answers` JSON NOT NULL COMMENT '问卷答案(JSON格式)',
  `risk_score` INT NOT NULL DEFAULT 0 COMMENT '风险评分',
  `is_focus_group` TINYINT NOT NULL DEFAULT 0 COMMENT '是否重点人群(0否/1是)',
  `submit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `create_source` VARCHAR(20) NOT NULL DEFAULT 'resident' COMMENT '创建来源(resident居民/surveyor调查员)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_surveyor_id` (`surveyor_id`),
  KEY `idx_is_focus_group` (`is_focus_group`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问卷记录表';

-- ----------------------------
-- Table structure for gc_blood_appointment (采血预约表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_blood_appointment`;
CREATE TABLE `gc_blood_appointment` (
  `appointment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `resident_id` BIGINT NOT NULL COMMENT '居民ID',
  `site_id` BIGINT NOT NULL COMMENT '采血点ID',
  `appointment_time` DATETIME NOT NULL COMMENT '预约时间',
  `appointment_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '预约状态(pending已预约/cancelled已取消/completed已完成)',
  `third_system_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方系统预约ID',
  `barcode_number` VARCHAR(50) DEFAULT NULL COMMENT '条码编号(第三方系统生成)',
  `push_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '推送状态(pending待推送/pushing推送中/pushed已推送/failed推送失败)',
  `push_time` DATETIME DEFAULT NULL COMMENT '推送时间',
  `retry_times` INT NOT NULL DEFAULT 0 COMMENT '重试次数',
  `sampling_status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '采样状态(0未采样/1已采样)',
  `sampling_time` DATETIME DEFAULT NULL COMMENT '采样时间',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`appointment_id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_push_status` (`push_status`),
  KEY `idx_third_system_id` (`third_system_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采血预约表';

-- ----------------------------
-- Table structure for gc_push_log (推送日志表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_push_log`;
CREATE TABLE `gc_push_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `appointment_id` BIGINT NOT NULL COMMENT '预约ID',
  `request_url` VARCHAR(500) NOT NULL COMMENT '请求URL',
  `request_body` TEXT NOT NULL COMMENT '请求体(JSON)',
  `response_code` INT DEFAULT NULL COMMENT '响应码',
  `response_body` TEXT DEFAULT NULL COMMENT '响应体(JSON)',
  `push_status` VARCHAR(20) NOT NULL COMMENT '推送状态(success成功/failed失败)',
  `retry_times` INT NOT NULL DEFAULT 0 COMMENT '重试次数',
  `push_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推送时间',
  `cost_time` INT DEFAULT NULL COMMENT '耗时(毫秒)',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`log_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_push_time` (`push_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推送日志表';

-- ----------------------------
-- Table structure for gc_task (任务表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_task`;
CREATE TABLE `gc_task` (
  `task_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `task_code` VARCHAR(50) NOT NULL COMMENT '任务编号',
  `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型(questionnaire问卷/blood采血)',
  `region_id` BIGINT NOT NULL COMMENT '分配区域ID',
  `target_count` INT NOT NULL DEFAULT 0 COMMENT '目标数量',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '任务说明',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '任务状态(draft草稿/ongoing进行中/finished已结束)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`task_id`),
  UNIQUE KEY `uk_task_code` (`task_code`),
  KEY `idx_region_id` (`region_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务表';

-- ----------------------------
-- Table structure for gc_screening_result (筛查结果表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_screening_result`;
CREATE TABLE `gc_screening_result` (
  `result_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `resident_id` BIGINT NOT NULL COMMENT '居民ID',
  `site_id` BIGINT NOT NULL COMMENT '筛查点ID',
  `blood_result` VARCHAR(20) DEFAULT NULL COMMENT '血液筛查结果(not_tested未检测/low_risk中低风险/high_risk高风险)',
  `blood_result_detail` TEXT DEFAULT NULL COMMENT '血液筛查详细结果',
  `blood_result_date` DATE DEFAULT NULL COMMENT '血液筛查日期',
  `gastroscopy_result` VARCHAR(20) DEFAULT NULL COMMENT '胃镜筛查结果(预留)',
  `gastroscopy_result_detail` TEXT DEFAULT NULL COMMENT '胃镜筛查详细结果(预留)',
  `gastroscopy_date` DATE DEFAULT NULL COMMENT '胃镜检查日期(预留)',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`result_id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_site_id` (`site_id`),
  KEY `idx_blood_result` (`blood_result`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='筛查结果表';

-- ----------------------------
-- Table structure for gc_follow_up (随访记录表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_follow_up`;
CREATE TABLE `gc_follow_up` (
  `follow_up_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '随访记录ID',
  `resident_id` BIGINT NOT NULL COMMENT '居民ID',
  `tracker_id` BIGINT DEFAULT NULL COMMENT '随访跟踪员ID',
  `tracker_name` VARCHAR(50) DEFAULT NULL COMMENT '随访跟踪员姓名',
  `follow_up_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '随访状态(pending待随访/ongoing随访中/completed已完成)',
  `follow_up_reason` VARCHAR(200) NOT NULL COMMENT '随访原因',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`follow_up_id`),
  KEY `idx_resident_id` (`resident_id`),
  KEY `idx_tracker_id` (`tracker_id`),
  KEY `idx_follow_up_status` (`follow_up_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='随访记录表';

-- ----------------------------
-- Table structure for gc_follow_up_track (随访跟踪记录表)
-- ----------------------------
DROP TABLE IF EXISTS `gc_follow_up_track`;
CREATE TABLE `gc_follow_up_track` (
  `track_record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '跟踪记录ID',
  `follow_up_id` BIGINT NOT NULL COMMENT '随访记录ID',
  `tracker_id` BIGINT NOT NULL COMMENT '跟踪员ID',
  `contact_date` DATE NOT NULL COMMENT '联系日期',
  `contact_content` TEXT NOT NULL COMMENT '联系内容',
  `next_contact_date` DATE DEFAULT NULL COMMENT '下次联系日期',
  `create_by` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`track_record_id`),
  KEY `idx_follow_up_id` (`follow_up_id`),
  KEY `idx_tracker_id` (`tracker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='随访跟踪记录表';

-- ----------------------------
-- 初始化数据字典
-- ----------------------------
-- 区域层级字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (100, '区域层级', 'gc_region_level', '0', 'admin', NOW(), '行政区划层级字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(100, 1, '省级', '1', 'gc_region_level', '0', 'admin', NOW()),
(101, 2, '市级', '2', 'gc_region_level', '0', 'admin', NOW()),
(102, 3, '区级', '3', 'gc_region_level', '0', 'admin', NOW()),
(103, 4, '街道/乡镇', '4', 'gc_region_level', '0', 'admin', NOW()),
(104, 5, '社区/村', '5', 'gc_region_level', '0', 'admin', NOW());

-- 任务类型字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (101, '任务类型', 'gc_task_type', '0', 'admin', NOW(), '胃癌筛查任务类型字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(105, 1, '问卷调查任务', 'questionnaire', 'gc_task_type', '0', 'admin', NOW()),
(106, 2, '采血筛查任务', 'blood', 'gc_task_type', '0', 'admin', NOW());

-- 任务状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (102, '任务状态', 'gc_task_status', '0', 'admin', NOW(), '任务状态字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(107, 1, '草稿', 'draft', 'gc_task_status', '0', 'admin', NOW()),
(108, 2, '进行中', 'ongoing', 'gc_task_status', '0', 'admin', NOW()),
(109, 3, '已结束', 'finished', 'gc_task_status', '0', 'admin', NOW());

-- 采样状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (103, '采样状态', 'gc_sampling_status', '0', 'admin', NOW(), '采样状态字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(110, 1, '未采样', '0', 'gc_sampling_status', '0', 'admin', NOW()),
(111, 2, '已采样', '1', 'gc_sampling_status', '0', 'admin', NOW());

-- 血液筛查结果字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (104, '血液筛查结果', 'gc_blood_result', '0', 'admin', NOW(), '血液筛查结果字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(112, 1, '未检测', 'not_tested', 'gc_blood_result', '0', 'admin', NOW()),
(113, 2, '中低风险', 'low_risk', 'gc_blood_result', '0', 'admin', NOW()),
(114, 3, '高风险', 'high_risk', 'gc_blood_result', '0', 'admin', NOW());

-- 随访状态字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) 
VALUES (105, '随访状态', 'gc_follow_up_status', '0', 'admin', NOW(), '随访状态字典');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time) VALUES
(115, 1, '待随访', 'pending', 'gc_follow_up_status', '0', 'admin', NOW()),
(116, 2, '随访中', 'ongoing', 'gc_follow_up_status', '0', 'admin', NOW()),
(117, 3, '已完成', 'completed', 'gc_follow_up_status', '0', 'admin', NOW());

SET FOREIGN_KEY_CHECKS = 1;
