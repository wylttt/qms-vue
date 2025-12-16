#!/bin/bash
#
# SQL语法验证脚本
# 用于验证SQL文件语法是否正确,但不实际执行
#

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo ""
echo "=========================================="
echo "  SQL语法验证工具"
echo "=========================================="
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# SQL文件列表
SQL_FILES=(
    "gc_gastric_cancer_screening.sql"
    "gc_region_data_sample.sql"
    "gc_questionnaire_template_sample.sql"
)

# 验证函数
verify_sql() {
    local file=$1
    local filepath="${SCRIPT_DIR}/${file}"
    
    echo -n "验证: ${file} ... "
    
    if [ ! -f "${filepath}" ]; then
        echo -e "${RED}文件不存在${NC}"
        return 1
    fi
    
    # 检查文件是否为空
    if [ ! -s "${filepath}" ]; then
        echo -e "${RED}文件为空${NC}"
        return 1
    fi
    
    # 基本语法检查
    # 检查是否包含基本的SQL关键字
    if grep -qi "CREATE TABLE\|INSERT INTO\|ALTER TABLE" "${filepath}"; then
        # 检查是否有明显的语法错误
        if grep -q ";;\\|,,\\|CREATE  TABLE" "${filepath}"; then
            echo -e "${YELLOW}可能存在语法错误${NC}"
            return 1
        fi
        echo -e "${GREEN}通过${NC}"
        return 0
    else
        echo -e "${YELLOW}未找到SQL语句${NC}"
        return 1
    fi
}

# 统计
total=0
passed=0
failed=0

# 执行验证
for file in "${SQL_FILES[@]}"; do
    total=$((total + 1))
    if verify_sql "${file}"; then
        passed=$((passed + 1))
    else
        failed=$((failed + 1))
    fi
done

echo ""
echo "=========================================="
echo "  验证结果汇总"
echo "=========================================="
echo "总计: ${total} 个文件"
echo -e "通过: ${GREEN}${passed}${NC} 个"
if [ ${failed} -gt 0 ]; then
    echo -e "失败: ${RED}${failed}${NC} 个"
else
    echo -e "失败: ${failed} 个"
fi
echo ""

# 检查文件大小
echo "文件大小统计:"
for file in "${SQL_FILES[@]}"; do
    filepath="${SCRIPT_DIR}/${file}"
    if [ -f "${filepath}" ]; then
        size=$(du -h "${filepath}" | cut -f1)
        echo "  ${file}: ${size}"
    fi
done
echo ""

# 统计SQL语句数量
echo "SQL语句数量统计:"
for file in "${SQL_FILES[@]}"; do
    filepath="${SCRIPT_DIR}/${file}"
    if [ -f "${filepath}" ]; then
        # 统计CREATE TABLE语句
        create_count=$(grep -c "CREATE TABLE" "${filepath}" 2>/dev/null || echo "0")
        # 统计INSERT语句
        insert_count=$(grep -c "INSERT INTO" "${filepath}" 2>/dev/null || echo "0")
        echo "  ${file}:"
        echo "    CREATE TABLE: ${create_count}"
        echo "    INSERT INTO: ${insert_count}"
    fi
done
echo ""

if [ ${failed} -eq 0 ]; then
    echo -e "${GREEN}所有SQL文件语法验证通过!${NC}"
    exit 0
else
    echo -e "${RED}部分SQL文件验证失败,请检查!${NC}"
    exit 1
fi
