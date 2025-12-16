/*
 行政区划数据示例 - 江西省九江市濂溪区
 
 说明:
 1. 本文件为示例数据,仅包含江西省九江市濂溪区及部分下属街道/乡镇数据
 2. 完整的全国行政区划数据请从以下来源获取:
    - 国家统计局: http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/
    - 民政部行政区划代码: https://www.mca.gov.cn/
    - 开源数据库: https://github.com/modood/Administrative-divisions-of-China
 3. 导入时请按照省、市、区、街道、社区的顺序依次导入,确保父级记录已存在
*/

SET NAMES utf8mb4;

-- ----------------------------
-- 省级数据 (江西省)
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) 
VALUES (360000, '江西省', '360000', 1, NULL, '0', 1, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 市级数据 (九江市)
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) 
VALUES (360400, '九江市', '360400', 2, 360000, '0', 1, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 区级数据 (濂溪区)
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) 
VALUES (360403, '濂溪区', '360403', 3, 360400, '0', 1, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 街道/乡镇级数据 (濂溪区下属街道/乡镇)
-- 注意: 以下为示例数据,实际街道/乡镇代码和名称请以官方数据为准
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) VALUES
(360403001, '十里街道', '360403001', 4, 360403, '0', 1, 'system', NOW()),
(360403002, '五里街道', '360403002', 4, 360403, '0', 2, 'system', NOW()),
(360403003, '姑塘镇', '360403003', 4, 360403, '0', 3, 'system', NOW()),
(360403004, '莲花镇', '360403004', 4, 360403, '0', 4, 'system', NOW()),
(360403005, '新港镇', '360403005', 4, 360403, '0', 5, 'system', NOW()),
(360403006, '高垅乡', '360403006', 4, 360403, '0', 6, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 社区/村级数据示例 (十里街道下属社区)
-- 注意: 以下为示例数据,实际社区/村代码和名称请以官方数据为准
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) VALUES
(360403001001, '前进社区', '360403001001', 5, 360403001, '0', 1, 'system', NOW()),
(360403001002, '滨兴社区', '360403001002', 5, 360403001, '0', 2, 'system', NOW()),
(360403001003, '向阳社区', '360403001003', 5, 360403001, '0', 3, 'system', NOW()),
(360403001004, '青年路社区', '360403001004', 5, 360403001, '0', 4, 'system', NOW()),
(360403001005, '浔南社区', '360403001005', 5, 360403001, '0', 5, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 社区/村级数据示例 (五里街道下属社区)
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) VALUES
(360403002001, '长虹社区', '360403002001', 5, 360403002, '0', 1, 'system', NOW()),
(360403002002, '五里社区', '360403002002', 5, 360403002, '0', 2, 'system', NOW()),
(360403002003, '龙开河社区', '360403002003', 5, 360403002, '0', 3, 'system', NOW()),
(360403002004, '德化社区', '360403002004', 5, 360403002, '0', 4, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 社区/村级数据示例 (姑塘镇下属村)
-- ----------------------------
INSERT INTO gc_region (region_id, region_name, region_code, region_level, parent_id, status, sort_order, create_by, create_time) VALUES
(360403003001, '姑塘村', '360403003001', 5, 360403003, '0', 1, 'system', NOW()),
(360403003002, '花果村', '360403003002', 5, 360403003, '0', 2, 'system', NOW()),
(360403003003, '东风村', '360403003003', 5, 360403003, '0', 3, 'system', NOW()),
(360403003004, '红光村', '360403003004', 5, 360403003, '0', 4, 'system', NOW())
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- ----------------------------
-- 查询验证数据
-- ----------------------------
-- 查询江西省及下属市
SELECT r1.region_name AS '省', r2.region_name AS '市'
FROM gc_region r1
LEFT JOIN gc_region r2 ON r2.parent_id = r1.region_id
WHERE r1.region_level = 1 AND r1.region_code = '360000';

-- 查询濂溪区及下属街道/乡镇
SELECT r1.region_name AS '区', r2.region_name AS '街道/乡镇'
FROM gc_region r1
LEFT JOIN gc_region r2 ON r2.parent_id = r1.region_id
WHERE r1.region_level = 3 AND r1.region_code = '360403'
ORDER BY r2.sort_order;

-- 查询十里街道下属社区
SELECT r1.region_name AS '街道', r2.region_name AS '社区'
FROM gc_region r1
LEFT JOIN gc_region r2 ON r2.parent_id = r1.region_id
WHERE r1.region_level = 4 AND r1.region_code = '360403001'
ORDER BY r2.sort_order;

/*
 导入全国行政区划数据说明:
 
 1. 下载数据源
    推荐使用: https://github.com/modood/Administrative-divisions-of-China
    或访问: https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov
    
 2. 数据格式转换
    将JSON/CSV格式的数据转换为SQL INSERT语句
    注意保持以下字段映射:
    - code -> region_code (行政区划代码)
    - name -> region_name (名称)
    - level -> region_level (层级: 1省/2市/3区/4街道/5社区)
    - parent_code -> 通过parent_code查找对应的parent_id
    
 3. 分批导入
    建议按省份分批导入,每个省份一个SQL文件
    导入顺序: 省 -> 市 -> 区 -> 街道 -> 社区
    
 4. 数据更新
    建议每年更新一次行政区划数据
    使用 ON DUPLICATE KEY UPDATE 语句避免重复数据
    
 5. 性能优化
    - 导入前关闭外键检查: SET FOREIGN_KEY_CHECKS = 0;
    - 导入后重建索引: OPTIMIZE TABLE gc_region;
    - 使用批量插入,每次插入500-1000条记录
*/
