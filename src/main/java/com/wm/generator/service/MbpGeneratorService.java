package com.wm.generator.service;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.wm.generator.common.ServiceException;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.config.ProjectPathResolver;
import com.wm.generator.dto.GenSetting;
import com.wm.generator.dto.OutputFileInfo;
import com.wm.generator.dto.UserConfig;
import com.wm.generator.mbp.BeetlTemplateEngine;
import com.wm.generator.mbp.NameConverter;
import com.wm.generator.mbp.TableInjectionConfig;
import com.wm.generator.strategy.EntityStrategy;
import com.wm.generator.util.PathUtil;
import com.wm.mybatis.plus.generator.AutoGenerator;
import com.wm.mybatis.plus.generator.config.*;
import com.wm.mybatis.plus.generator.config.po.TableField;
import com.wm.mybatis.plus.generator.config.po.TableFill;
import com.wm.mybatis.plus.generator.config.po.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MbpGeneratorService {

    private AutoGenerator mpg;

    @Resource
    private DataSourceConfig ds;

    @Resource
    private GeneratorConfig generatorConfig;

    @Resource
    private UserConfigStore userConfigStore;

    @Resource
    private ProjectPathResolver projectPathResolver;

    @Resource
    private BeetlTemplateEngine beetlTemplateEngine;

    @PostConstruct
    public void initGenerator() {
        this.mpg = new AutoGenerator();
        mpg.setDataSource(ds);
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        //不使用默认的配置,所有的文件都改为自定义生成
        templateConfig.disable(TemplateType.CONTROLLER,
                TemplateType.ENTITY,
                TemplateType.MAPPER,
                TemplateType.DAO,
                TemplateType.SERVICE,
                TemplateType.XML);
        mpg.setTemplate(templateConfig);
        GlobalConfig gc = new GlobalConfig();
        gc.setOpen(false);
        gc.setDateType(generatorConfig.getDateType());
        mpg.setGlobalConfig(gc);
        mpg.setTemplateEngine(beetlTemplateEngine);
    }

    public void genCodeBatch(GenSetting genSetting, List<String> tables) {
        checkGenSetting(genSetting);
        projectPathResolver.refreshBaseProjectPath(genSetting.getRootPath());
        //自定义参数配置
        mpg.setCfg(new TableInjectionConfig(generatorConfig, genSetting));
        //生成策略配置
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        BeanUtils.copyProperties(userConfig.getEntityStrategy(), mpg.getGlobalConfig());
        mpg.getGlobalConfig().setAuthor(genSetting.getAuthor());
        mpg.getGlobalConfig().setFileOverride(genSetting.isOverride());
        mpg.getGlobalConfig().setIdType(generatorConfig.getIdType());
        StrategyConfig strategy = getCurrentStrategy(userConfig);
        NameConverter nameConverter = generatorConfig.getAvailableNameConverter();
        strategy.setNameConvert(new INameConvert() {
            @Override
            public String entityNameConvert(TableInfo tableInfo) {
                return nameConverter.entityNameConvert(tableInfo.getName());
            }

            @Override
            public String propertyNameConvert(TableField field) {
                return nameConverter.propertyNameConvert(field.getName());
            }
        });
        mpg.setStrategy(strategy);
        //设置java代码的包名
        PackageConfig pc = new PackageConfig();
        pc.setParent(null);
        pc.setController(PathUtil.joinPackage(userConfig.getControllerInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setEntity(PathUtil.joinPackage(userConfig.getEntityInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setMapper(PathUtil.joinPackage(userConfig.getMapperInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setDao(PathUtil.joinPackage(userConfig.getDaoInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setXml(PathUtil.joinPackage(userConfig.getMapperXmlInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setService(PathUtil.joinPackage(userConfig.getServiceInfo().getOutputPackage(), genSetting.getModuleName()));
        pc.setServiceImpl(PathUtil.joinPackage(userConfig.getServiceImplInfo().getOutputPackage(), genSetting.getModuleName()));
        mpg.setPackageInfo(pc);
        for (String table : tables) {
            //设置各类文件的名称
            mpg.getGlobalConfig().setEntityName(nameConverter.entityNameConvert(table) + generatorConfig.getEntitySuffix());
            mpg.getGlobalConfig().setMapperName(nameConverter.mapperNameConvert(table));
            mpg.getGlobalConfig().setDaoName(nameConverter.daoNameConvert(table));
            mpg.getGlobalConfig().setXmlName(nameConverter.mapperXmlNameConvert(table));
            mpg.getGlobalConfig().setServiceName(nameConverter.serviceNameConvert(table));
            mpg.getGlobalConfig().setServiceImplName(nameConverter.serviceImplNameConvert(table));
            mpg.getGlobalConfig().setControllerName(nameConverter.controllerNameConvert(table));
            mpg.getGlobalConfig().setBaseResultMap(userConfig.getMapperXmlStrategy().isBaseResultMap());
            mpg.getGlobalConfig().setEnableCache(userConfig.getMapperXmlStrategy().isEnableCache());
            genCode(mpg, genSetting, userConfig, table);
        }
    }

    private void genCode(AutoGenerator ag, GenSetting genSetting, UserConfig userConfig, String tableName) {
        //根据用户的选择，添加输出文件
        List<FileOutConfig> focList = new ArrayList<>();
        for (OutputFileInfo outputFileInfo : userConfig.getOutputFiles()) {
            if (genSetting.getChoosedOutputFiles().contains(outputFileInfo.getFileType())) {
                NameConverter nameConverter = generatorConfig.getAvailableNameConverter();
                String fileName = nameConverter.outputFileNameConvert(outputFileInfo.getFileType(), tableName, generatorConfig);
                String outputDir = projectPathResolver.convertPackageToPath(outputFileInfo.getOutputLocation());
                String filePath = PathUtil.joinPath(outputDir, genSetting.getModuleName(), fileName);
                focList.add(new FileOutConfig(outputFileInfo.getAvailableTemplatePath()) {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        //避免使用了NameConverter导致entityTableFieldAnnotationEnable属性失效的问题，在此强制进行设置
                        if (userConfig.getEntityStrategy().isEntityTableFieldAnnotationEnable()) {
                            tableInfo.getFields().forEach(field -> {
                                field.setConvert(true);
                            });
                            tableInfo.getImportPackages().add("com.baomidou.mybatisplus.annotation.TableField");
                        }
                        return filePath;
                    }
                });
            }
        }
        ag.getStrategy().setInclude(tableName);
        ag.getCfg().setFileOutConfigList(focList);
        //清除config，强制重新创建，否则会导致数据表不刷新
        ag.setConfig(null);
        ag.execute();
    }

    private StrategyConfig getCurrentStrategy(UserConfig userConfig) {
        //生成策略配置
        StrategyConfig strategy = new StrategyConfig();
        EntityStrategy entityStrategy = userConfig.getEntityStrategy();
        //单独设置class属性，否则为空时复制属性会报错
        if (!Strings.isNullOrEmpty(entityStrategy.getSuperEntityClass())) {
            strategy.setSuperEntityClass(entityStrategy.getSuperEntityClass());
        }
        if (entityStrategy.getTableFills() != null && !entityStrategy.getTableFills().isEmpty()) {
            List<TableFill> tableFills = Lists.newArrayList();
            for (String tableFillStr : entityStrategy.getTableFills()) {
                String[] tmp = tableFillStr.split(":");
                TableFill tableFill = new TableFill(tmp[0], FieldFill.valueOf(tmp[1].toUpperCase()));
                tableFills.add(tableFill);
            }
            strategy.setTableFillList(tableFills);
        }
        if (entityStrategy.getSuperEntityColumns() != null && !entityStrategy.getSuperEntityColumns().isEmpty()) {
            strategy.setSuperEntityColumns(entityStrategy.getSuperEntityColumns().toArray(new String[]{}));
        }
        BeanUtils.copyProperties(userConfig.getControllerStrategy(), strategy);
        BeanUtils.copyProperties(entityStrategy, strategy, "superEntityClass", "tableFills", "superEntityColumns");
        BeanUtils.copyProperties(userConfig.getMapperStrategy(), strategy);
        BeanUtils.copyProperties(userConfig.getMapperXmlStrategy(), strategy);
        BeanUtils.copyProperties(userConfig.getServiceImplStrategy(), strategy);
        BeanUtils.copyProperties(userConfig.getServiceStrategy(), strategy);
        BeanUtils.copyProperties(userConfig.getServiceStrategy(), strategy);
        return strategy;
    }


    private void checkGenSetting(GenSetting genSetting) {
        if (Strings.isNullOrEmpty(genSetting.getRootPath())) {
            throw new ServiceException("目标项目根目录不能为空");
        }
        genSetting.setRootPath(projectPathResolver.getUTF8String(genSetting.getRootPath()));
        if (!FileUtil.isDirectory(genSetting.getRootPath())) {
            throw new ServiceException("目标项目根目录错误，请确认目录有效且存在：" + genSetting.getRootPath());
        }
        if (!genSetting.getRootPath().endsWith(File.separator)) {
            genSetting.setRootPath(genSetting.getRootPath() + File.separator);
        }
    }


}
