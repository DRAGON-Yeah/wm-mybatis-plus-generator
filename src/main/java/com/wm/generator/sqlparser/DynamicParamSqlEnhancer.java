package com.wm.generator.sqlparser;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.google.common.base.Strings;
import com.wm.generator.common.ServiceException;
import com.wm.mybatis.plus.generator.config.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class DynamicParamSqlEnhancer {

    private DataSourceConfig dataSourceConfig;

    public DynamicParamSqlEnhancer(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    /**
     * 解析SQL中所有的where动态条件
     *
     * @param sql
     * @return
     */
    public List<ConditionExpr> parseSqlDynamicConditions(String sql) {
        if (Strings.isNullOrEmpty(sql)) {
            throw new ServiceException("sql不能为空");
        }
        try {
            CCJSqlParser ccjSqlParser = new CCJSqlParser(sql);
            Statement statement = ccjSqlParser.Statement();
            if (statement instanceof Select) {
                SelectConditionParser parser = new SelectConditionParser();
                Select select = (Select) statement;
                select.getSelectBody().accept(parser);
                return parser.getParsedConditions();
            } else {
                throw new ServiceException("只能处理SQL查询语句");
            }
        } catch (Exception e) {
            log.error("解析SQL条件发生错误", e);
            throw new ServiceException("解析SQL条件发生错误,请检查SQL语法");
        }
    }

    /**
     * 将SQL中标记的动态条件替换为Mybatis动态SQL
     */
    public String enhanceDynamicConditions(String sql) {
        List<ConditionExpr> conditions = parseSqlDynamicConditions(sql);
        for (ConditionExpr condition : conditions) {
            Pattern pattern = Pattern.compile(condition.getFindPattern(), Pattern.CASE_INSENSITIVE);
            sql = ReUtil.replaceAll(sql, pattern, toDynamicSql(condition));
        }
        return sql;
    }

    /**
     * 去掉SQL中不符合语法的动态条件
     */
    public String clearIllegalStatements(String sql) {
        List<ConditionExpr> conditions = parseSqlDynamicConditions(sql);
        for (ConditionExpr condition : conditions) {
            if ("in".equalsIgnoreCase(condition.getOperator())) {
                Pattern pattern = Pattern.compile(condition.getFindPattern(), Pattern.CASE_INSENSITIVE);
                String replaceStr = " " + condition.getLogicOperator() + " " + condition.getLeftExpr() + " in ('')";
                sql = ReUtil.replaceAll(sql, pattern, replaceStr);
            }
        }
        return sql;
    }

    public String toDynamicSql(ConditionExpr condition) {
        StringBuilder tmp = new StringBuilder();
        if (Strings.isNullOrEmpty(condition.getLogicOperator())) {
            tmp.append(" 1=1 ");
        }
        tmp.append("\n<if test=\"");
        tmp.append(trimQuotes(condition.getParamNames().get(0)));
        tmp.append("!=null\">");
        tmp.append("\n ");
        if (!Strings.isNullOrEmpty(condition.getLogicOperator())) {
            tmp.append(condition.getLogicOperator());
        } else {
            tmp.append(" AND ");
        }
        tmp.append(" ");
        tmp.append(trimQuotes(condition.getLeftExpr()));
        tmp.append(" ");
        tmp.append(condition.getOperator());
        tmp.append(" ");
        if ("IN".equalsIgnoreCase(condition.getOperator())) {
            tmp.append("\n <foreach item=\"item\" collection=\"" + trimQuotes(condition.getRightExpr()) + "\" open=\"(\" separator=\",\" close=\")\">");
            tmp.append("\n #{item}");
            tmp.append("\n </foreach>");
        } else if ("LIKE".equalsIgnoreCase(condition.getOperator())) {
            tmp.append(getLikeConcatSql(trimQuotes(condition.getRightExpr())));
        } else {
            tmp.append(trimQuotes(condition.getRightExpr()));
            if (!Strings.isNullOrEmpty(condition.getMiddleOperator())) {
                tmp.append(" ");
                tmp.append(condition.getMiddleOperator());
                tmp.append(" ");
                tmp.append(trimQuotes(condition.getEndExpr()));
            }
        }
        tmp.append("\n</if>");
        return tmp.toString();
    }

    private String trimQuotes(String str) {
        if (Strings.isNullOrEmpty(str)
                || !str.startsWith("'")) {
            return str;
        }
        return str.substring(1, str.length() - 1);
    }

    private String getLikeConcatSql(String colExpr) {
        if (DbType.MYSQL == dataSourceConfig.getDbType()) {
            return "concat('%'," + colExpr + ",'%')";
        } else if (DbType.POSTGRE_SQL == dataSourceConfig.getDbType()) {
            return "concat('%'," + colExpr + ",'%')";
        } else if (DbType.ORACLE == dataSourceConfig.getDbType()) {
            return "'%' || " + colExpr + " ||'%'";
        } else if (DbType.SQL_SERVER == dataSourceConfig.getDbType()) {
            return "concat('%'," + colExpr + ",'%')";
        }
        return "concat('%'," + colExpr + ",'%')";
    }

}
