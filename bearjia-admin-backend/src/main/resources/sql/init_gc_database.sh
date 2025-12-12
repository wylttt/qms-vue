#!/bin/bash
#
# 胃癌筛查系统数据库初始化脚本
# 
# 使用方法:
#   chmod +x init_gc_database.sh
#   ./init_gc_database.sh
#
# 注意:
#   1. 执行前请确保MySQL服务已启动
#   2. 请根据实际情况修改数据库连接信息
#   3. 建议在测试环境先执行,验证无误后再在生产环境执行
#

# 数据库连接信息
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_NAME="bear_jia"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查MySQL是否可连接
check_mysql() {
    log_info "检查MySQL连接..."
    mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p -e "SELECT 1;" > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        log_error "无法连接到MySQL,请检查连接信息或MySQL服务是否启动"
        exit 1
    fi
    log_info "MySQL连接成功"
}

# 检查数据库是否存在
check_database() {
    log_info "检查数据库 ${DB_NAME} 是否存在..."
    mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p -e "USE ${DB_NAME};" > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        log_warn "数据库 ${DB_NAME} 不存在,将创建数据库"
        mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p -e "CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"
        log_info "数据库 ${DB_NAME} 创建成功"
    else
        log_info "数据库 ${DB_NAME} 已存在"
    fi
}

# 执行SQL文件
execute_sql() {
    local sql_file=$1
    local description=$2
    
    log_info "执行: ${description}"
    log_info "文件: ${sql_file}"
    
    if [ ! -f "${sql_file}" ]; then
        log_error "文件不存在: ${sql_file}"
        return 1
    fi
    
    mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p ${DB_NAME} < ${sql_file}
    if [ $? -eq 0 ]; then
        log_info "${description} - 执行成功"
        return 0
    else
        log_error "${description} - 执行失败"
        return 1
    fi
}

# 主流程
main() {
    echo ""
    echo "=========================================="
    echo "  胃癌筛查系统数据库初始化脚本"
    echo "=========================================="
    echo ""
    
    # 获取脚本所在目录
    SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    # 步骤1: 检查MySQL连接
    check_mysql
    
    # 步骤2: 检查数据库
    check_database
    
    # 步骤3: 执行核心表结构
    execute_sql "${SCRIPT_DIR}/gc_gastric_cancer_screening.sql" "创建核心业务表结构"
    if [ $? -ne 0 ]; then
        log_error "核心表结构创建失败,终止执行"
        exit 1
    fi
    
    # 步骤4: 导入行政区划示例数据
    log_warn "即将导入行政区划示例数据(仅包含江西省九江市濂溪区数据)"
    read -p "是否继续? (y/n): " choice
    if [ "$choice" == "y" ] || [ "$choice" == "Y" ]; then
        execute_sql "${SCRIPT_DIR}/gc_region_data_sample.sql" "导入行政区划示例数据"
    else
        log_warn "跳过行政区划数据导入"
    fi
    
    # 步骤5: 导入问卷模板示例数据
    log_warn "即将导入胃癌筛查问卷模板示例数据"
    read -p "是否继续? (y/n): " choice
    if [ "$choice" == "y" ] || [ "$choice" == "Y" ]; then
        execute_sql "${SCRIPT_DIR}/gc_questionnaire_template_sample.sql" "导入问卷模板示例数据"
    else
        log_warn "跳过问卷模板数据导入"
    fi
    
    # 步骤6: 验证数据
    log_info "验证数据库表..."
    TABLE_COUNT=$(mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p ${DB_NAME} -N -e "SHOW TABLES LIKE 'gc_%';" | wc -l)
    log_info "共创建 ${TABLE_COUNT} 个胃癌筛查业务表"
    
    log_info "验证行政区划数据..."
    REGION_COUNT=$(mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p ${DB_NAME} -N -e "SELECT COUNT(*) FROM gc_region;")
    log_info "行政区划记录数: ${REGION_COUNT}"
    
    log_info "验证问卷模板数据..."
    TEMPLATE_COUNT=$(mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p ${DB_NAME} -N -e "SELECT COUNT(*) FROM gc_questionnaire_template;")
    log_info "问卷模板数: ${TEMPLATE_COUNT}"
    
    echo ""
    echo "=========================================="
    log_info "数据库初始化完成!"
    echo "=========================================="
    echo ""
    log_info "下一步操作建议:"
    echo "  1. 如需全国行政区划数据,请从以下来源获取并导入:"
    echo "     - https://github.com/modood/Administrative-divisions-of-China"
    echo "  2. 配置后端application.yml数据库连接"
    echo "  3. 启动后端服务,验证业务功能"
    echo ""
}

# 执行主流程
main
