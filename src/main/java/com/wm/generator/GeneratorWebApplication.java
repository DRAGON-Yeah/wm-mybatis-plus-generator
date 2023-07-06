package com.wm.generator;


import com.baomidou.mybatisplus.annotation.IdType;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.config.MybatisPlusToolsApplication;
import com.wm.generator.dto.Constant;
import com.wm.generator.mbp.NameConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * web 启动程序
 */
public class GeneratorWebApplication {

    public static void main(String[] args) {

        Integer serverPort = 8088;
//        String dbUrl = "localhost:3306";
//        String basePackage = "com.wm.mall.settings";
//        String dbName = "db_e_commerce_center";
//        String dbUserName = "root";
//        String dbPassword = "root";

        String serverPortStr = System.getProperty("serverPort");
        if (StringUtils.isNotEmpty(serverPortStr)){
            serverPort = Integer.parseInt(serverPortStr);
        }

        String dbUrl = System.getProperty("dbUrl");
        String dbName = System.getProperty("dbName");
        String dbUserName = System.getProperty("dbUserName");
        String dbPassword = System.getProperty("dbPassword");
        String basePackage = System.getProperty("basePackage");

        int beginIndex = 1;
        String beginIndexStr = System.getProperty("nameBeginIndex");
        if (StringUtils.isNotEmpty(beginIndexStr)){
            beginIndex = Integer.parseInt(beginIndexStr);
        }
        final int index = beginIndex;

        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:mysql://" + dbUrl + "/" + dbName)
                .userName(dbUserName)
                .password(dbPassword)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                //数据库schema，POSTGRE_SQL,ORACLE,DB2类型的数据库需要指定
                .schemaName(dbName)
                //数据库id
                .idType(IdType.NONE)
                //数据模型后缀
                .entitySuffix(Constant.FILE_NAME_ENTITY_SUFFIX)
                //如果需要修改各类生成文件的默认命名规则，可自定义一个NameConverter实例，覆盖相应的名称转换方法：
                .nameConverter(new NameConverter() {

                    @Override
                    public String entityNameConvert(String tableName) {
                        tableName = table2Java(tableName);
                        tableName = tableName.substring(index, tableName.length());
                        return tableName;
                    }

                    /**
                     * 自定义Service类文件的名称规则
                     */
                    @Override
                    public String serviceNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Service";
                    }

                    /**
                     * 自定义Controller类文件的名称规则
                     */
                    @Override
                    public String controllerNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Controller";
                    }
                })
                .basePackage(basePackage)
                .port(serverPort)
                .build();
        MybatisPlusToolsApplication.run(config);
    }

}
