package com.wm.generator.mbp;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.dto.Constant;

import static com.wm.generator.dto.Constant.DOT_JAVA;
import static com.wm.generator.dto.Constant.DOT_XML;

/**
 * 自定义各类名称转换的规则
 */
public interface NameConverter {

    /**
     * 自定义Entity.java的类名称
     *
     * @param tableName 表名称
     * @return
     */
    default String entityNameConvert(String tableName) {
        if (Strings.isNullOrEmpty(tableName)) {
            return "";
        }
        tableName = table2Java(tableName);
        return tableName;
    }


    /**
     * 表名称变为驼峰法
     *
     * @param tableName
     * @return
     */
    default String table2Java(String tableName) {
        String res = tableName.toLowerCase();
        while (res.contains("_")) {
            int index = res.indexOf("_");
            String target = res.substring(index + 1, index + 2);
            res = res.replace(("_" + target), target.toUpperCase());
        }
        return StrUtil.upperFirst(res);
    }

    /**
     * 自定义表字段名到实体类属性名的转换规则
     *
     * @param fieldName 表字段名称
     * @return
     */
    default String propertyNameConvert(String fieldName) {
        if (Strings.isNullOrEmpty(fieldName)) {
            return "";
        }
        if (fieldName.contains("_")) {
            return StrUtil.toCamelCase(fieldName.toLowerCase());
        }
        return fieldName;
    }

    /**
     * 自定义Mapper.java的类名称
     */
    default String mapperNameConvert(String tableName) {
        return entityNameConvert(tableName) + "Mapper";
    }

    /**
     * 自定义Mapper.xml的文件名称
     */
    default String mapperXmlNameConvert(String tableName) {
        return entityNameConvert(tableName) + "Mapper";
    }

    /**
     * 自定义Service.java的类名称
     */
    default String serviceNameConvert(String tableName) {
        return "I" + entityNameConvert(tableName) + "Service";
    }

    /**
     * 自定义Dao.java的类名称
     */
    default String daoNameConvert(String tableName) {
        return entityNameConvert(tableName) + "Dao";
    }

    /**
     * 自定义ServiceImpl.java的类名称
     */
    default String serviceImplNameConvert(String tableName) {
        return entityNameConvert(tableName) + "ServiceImpl";
    }

    /**
     * 自定义Controller.java的类名称
     */
    default String controllerNameConvert(String tableName) {
        return entityNameConvert(tableName) + "Controller";
    }

    /**
     * 自定义其它生成文件的文件名（不包括entity,mapper.java,mapper.xml,service,serviceImpl,controller这6种）
     *
     * @param fileType  在页面上输入的输出文件标识
     * @param tableName 关联的数据表名称名称
     * @return 生成文件的名称，带后缀
     */
    default String outputFileNameConvert(String fileType, String tableName, GeneratorConfig generatorConfig) {
        if (fileType.equals(Constant.FILE_TYPE_ENTITY)) {
            return this.entityNameConvert(tableName) + generatorConfig.getEntitySuffix() + DOT_JAVA;
        } else if (fileType.equals(Constant.FILE_TYPE_MAPPER)) {
            return this.mapperNameConvert(tableName) + DOT_JAVA;
        } else if (fileType.equals(Constant.FILE_TYPE_MAPPER_XML)) {
            return this.mapperXmlNameConvert(tableName) + DOT_XML;
        } else if (fileType.equals(Constant.FILE_TYPE_SERVICE)) {
            return this.serviceNameConvert(tableName) + DOT_JAVA;
        } else if (fileType.equals(Constant.FILE_TYPE_DAO)) {
            return this.daoNameConvert(tableName) + DOT_JAVA;
        } else if (fileType.equals(Constant.FILE_TYPE_SERVICE_IMPL)) {
            return this.serviceImplNameConvert(tableName) + DOT_JAVA;
        } else if (fileType.equals(Constant.FILE_TYPE_CONTROLLER)) {
            return this.controllerNameConvert(tableName) + DOT_JAVA;
        }
        return this.entityNameConvert(tableName) + fileType;
    }

}
