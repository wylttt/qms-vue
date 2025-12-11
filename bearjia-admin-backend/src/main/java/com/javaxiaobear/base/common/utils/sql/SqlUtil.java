package com.javaxiaobear.base.common.utils.sql;

import com.javaxiaobear.base.common.exception.UtilException;
import com.javaxiaobear.base.common.utils.StringUtils;

/**
 * sql操作工具类
 * 
 * @author javaxiaobear
 */
public class SqlUtil
{
    /**
     * 定义常用的 sql关键字（用于查询参数检查）
     */
    public static String SQL_REGEX = "and |extractvalue|updatexml|exec |chr |mid |master |char |declare |or |+|user()";

    /**
     * 定义危险的SQL注入关键字（用于DDL语句检查）
     */
    public static String[] DANGEROUS_KEYWORDS = {
        "extractvalue(", "updatexml(", "load_file(", "into outfile", "into dumpfile",
        "union select", "information_schema.", "mysql.user", "sleep(", "benchmark(",
        "user()", "exec ", "declare ", "master."
    };

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 限制orderBy最大长度
     */
    private static final int ORDER_BY_MAX_LENGTH = 500;

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value)
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
        {
            throw new UtilException("参数不符合规范，不能进行查询");
        }
        if (StringUtils.length(value) > ORDER_BY_MAX_LENGTH)
        {
            throw new UtilException("参数已超过最大限制，不能进行查询");
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value)
    {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查（用于查询参数）
     */
    public static void filterKeyword(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return;
        }
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords)
        {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1)
            {
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }

    /**
     * DDL语句安全检查（用于创建表等DDL操作）
     */
    public static void filterDdlKeyword(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return;
        }

        // 检查是否只包含CREATE TABLE语句
        String upperValue = value.toUpperCase().trim();
        if (!upperValue.startsWith("CREATE TABLE"))
        {
            throw new UtilException("只允许CREATE TABLE语句");
        }

        // 移除注释和字符串字面量后再检查危险关键字
        String cleanValue = removeCommentsAndStrings(value);
        String lowerCleanValue = cleanValue.toLowerCase();

        // 只检查最核心的危险关键字
        String[] coreKeywords = {
            "union select", "information_schema.", "mysql.user",
            "load_file(", "into outfile", "into dumpfile"
        };

        for (String keyword : coreKeywords)
        {
            if (lowerCleanValue.contains(keyword))
            {
                throw new UtilException("DDL语句存在安全风险: " + keyword);
            }
        }

        // 检查是否包含多个语句（防止语句注入）
        String[] statements = value.split(";");
        int validStatements = 0;
        for (String statement : statements)
        {
            String trimmedStatement = statement.trim();
            if (StringUtils.isNotEmpty(trimmedStatement))
            {
                validStatements++;
                String upperStatement = trimmedStatement.toUpperCase();
                if (!upperStatement.startsWith("CREATE TABLE"))
                {
                    throw new UtilException("只允许CREATE TABLE语句");
                }
            }
        }

        // 限制只能有一个CREATE TABLE语句
        if (validStatements > 1)
        {
            throw new UtilException("只允许单个CREATE TABLE语句");
        }
    }

    /**
     * 移除SQL中的注释和字符串字面量，用于安全检查
     */
    private static String removeCommentsAndStrings(String sql)
    {
        if (StringUtils.isEmpty(sql))
        {
            return sql;
        }

        StringBuilder result = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inComment = false;

        for (int i = 0; i < sql.length(); i++)
        {
            char c = sql.charAt(i);
            char next = (i + 1 < sql.length()) ? sql.charAt(i + 1) : '\0';

            if (!inSingleQuote && !inDoubleQuote && !inComment)
            {
                if (c == '\'' && next != '\'')
                {
                    inSingleQuote = true;
                    result.append(' '); // 用空格替换字符串
                }
                else if (c == '"' && next != '"')
                {
                    inDoubleQuote = true;
                    result.append(' '); // 用空格替换字符串
                }
                else if (c == '-' && next == '-')
                {
                    inComment = true;
                    i++; // 跳过第二个-
                }
                else if (c == '/' && next == '*')
                {
                    inComment = true;
                    i++; // 跳过*
                }
                else
                {
                    result.append(c);
                }
            }
            else if (inSingleQuote)
            {
                if (c == '\'' && next != '\'')
                {
                    inSingleQuote = false;
                }
                result.append(' '); // 用空格替换字符串内容
            }
            else if (inDoubleQuote)
            {
                if (c == '"' && next != '"')
                {
                    inDoubleQuote = false;
                }
                result.append(' '); // 用空格替换字符串内容
            }
            else if (inComment)
            {
                if (c == '\n' || c == '\r')
                {
                    inComment = false;
                    result.append(c);
                }
                else if (c == '*' && next == '/')
                {
                    inComment = false;
                    i++; // 跳过/
                }
                // 注释内容不添加到结果中
            }
        }

        return result.toString();
    }
}
