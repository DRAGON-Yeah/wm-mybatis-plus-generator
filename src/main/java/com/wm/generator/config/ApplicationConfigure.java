package com.wm.generator.config;

import cn.hutool.core.util.StrUtil;
import com.wm.generator.mbp.BeetlTemplateEngine;
import com.wm.generator.service.UserConfigStore;
import com.wm.generator.sqlparser.DynamicParamSqlEnhancer;
import com.wm.mybatis.plus.generator.config.DataSourceConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfigure {

    @Bean
    public ProjectPathResolver projectPathResolver(GeneratorConfig config) {
        return new ProjectPathResolver(config.getBasePackage());
    }

    @Bean
    public DataSource dataSource(GeneratorConfig config) {
        if (StrUtil.isBlank(config.getJdbcUrl())) {
            throw new IllegalArgumentException("必须指定jdbcUrl用于创建数据源");
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(config.getJdbcUrl());
        ds.setUsername(config.getUserName());
        ds.setPassword(config.getPassword());
        ds.setDriverClassName(config.getDriverClassName());
        return ds;
    }

    @Bean
    public DataSourceConfig dataSourceConfig(GeneratorConfig config) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(config.getJdbcUrl());
        dataSourceConfig.setDriverName(config.getDriverClassName());
        dataSourceConfig.setUsername(config.getUserName());
        dataSourceConfig.setPassword(config.getPassword());
        dataSourceConfig.setSchemaName(config.getSchemaName());
        dataSourceConfig.setTypeConvert(config.getTypeConvert());
        return dataSourceConfig;
    }

    @Bean
    public JdbcTemplate dbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public BeetlTemplateEngine beetlTemplateEngine(UserConfigStore userConfigStore) {
        return new BeetlTemplateEngine(userConfigStore.getTemplateStoreDir());
    }

    @Bean
    public DynamicParamSqlEnhancer dynamicParamSqlEnhancer(DataSourceConfig dsc) {
        return new DynamicParamSqlEnhancer(dsc);
    }
}
