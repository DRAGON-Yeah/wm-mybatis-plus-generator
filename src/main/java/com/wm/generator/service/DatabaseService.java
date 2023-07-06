package com.wm.generator.service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.google.common.collect.Lists;
import com.wm.generator.dto.TableInfo;
import com.wm.mybatis.plus.generator.config.DataSourceConfig;
import com.wm.mybatis.plus.generator.config.IDbQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private DataSourceConfig dataSourceConfig;

    public List<TableInfo> getTablesFromDb() {
        IDbQuery dbQuery = dataSourceConfig.getDbQuery();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(getTableSql());
        List<TableInfo> tableInfos = Lists.newArrayList();
        for (Map<String, Object> table : results) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setName((String) table.get(dbQuery.tableName()));
            tableInfo.setComment((String) table.get(dbQuery.tableComment()));
            tableInfos.add(tableInfo);
        }
        return tableInfos;
    }

    public String getTableSql() {
        String tablesSql = dataSourceConfig.getDbQuery().tablesSql();
        if (DbType.POSTGRE_SQL == dataSourceConfig.getDbType()) {
            String schema = dataSourceConfig.getSchemaName();
            if (schema == null) {
                //pg 默认 schema=public
                schema = "public";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.KINGBASE_ES == dataSourceConfig.getDbType()) {
            String schema = dataSourceConfig.getSchemaName();
            if (schema == null) {
                //kingbase 默认 schema=PUBLIC
                schema = "PUBLIC";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.DB2 == dataSourceConfig.getDbType()) {
            String schema = dataSourceConfig.getSchemaName();
            if (schema == null) {
                //db2 默认 schema=current schema
                schema = "current schema";
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.ORACLE == dataSourceConfig.getDbType()) {
            String schema = dataSourceConfig.getSchemaName();
            //oracle 默认 schema=username
            if (schema == null) {
                schema = dataSourceConfig.getUsername().toUpperCase();
                dataSourceConfig.setSchemaName(schema);
            }
            tablesSql = String.format(tablesSql, schema);
        }
        return tablesSql;
    }

}
