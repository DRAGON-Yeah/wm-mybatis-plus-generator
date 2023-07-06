package com.wm.generator.dto;

public class Constant {

    public final static String DOT_JAVA = ".java";

    public final static String DOT_XML = ".xml";

    /**
     * 生成文件的输出目录
     */
    public final static String CONFIG_HOME = System.getProperty("user.dir");
    /**
     * 输出目录
     */
    public final static String TEMPLATE_STORE_DIR = "temp/code-generator";

    public final static String RESOURCE_PREFIX_CLASSPATH = "classpath:";

    public final static String RESOURCE_PREFIX_FILE = "file:";

    public final static String PACKAGE_RESOURCES_PREFIX = "resources:";

    public final static String PACKAGE_JAVA_PREFIX = "java:";

    public static final String FILE_NAME_ENTITY_SUFFIX = "DO";

    public final static String FILE_TYPE_ENTITY = "Entity";

    public final static String FILE_TYPE_MAPPER = "Mapper.java";

    public final static String FILE_TYPE_DAO = "Dao";

    public final static String FILE_TYPE_CONTROLLER = "Controller";

    public final static String FILE_TYPE_MAPPER_XML = "Mapper.xml";

    public final static String FILE_TYPE_SERVICE = "Service";

    public final static String FILE_TYPE_SERVICE_IMPL = "ServiceImpl";

}
