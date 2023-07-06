package com.wm.generator.service;

import com.google.common.collect.Lists;
import com.wm.generator.common.ServiceException;
import com.wm.generator.config.ProjectPathResolver;
import com.wm.generator.dto.OutputFileInfo;
import com.wm.generator.dto.UserConfig;
import com.wm.generator.strategy.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.wm.generator.dto.Constant.*;

@Service
public class OutputFileInfoService {

    @Resource
    private ProjectPathResolver pathResolver;

    @Resource
    private UserConfigStore userConfigStore;

    @Resource
    private TemplateService templateService;

    @Resource
    private OutputFileInfoService outputFileInfoService;

    public List<OutputFileInfo> getBuiltInFileInfo() {
        List<OutputFileInfo> builtInFiles = Lists.newArrayList();
        //Entity
        OutputFileInfo entityFile = new OutputFileInfo();
        entityFile.setBuiltIn(true);
        entityFile.setFileType(FILE_TYPE_ENTITY);
        entityFile.setOutputLocation(pathResolver.resolveEntityPackage());
        entityFile.setTemplateName(templateService.fileType2TemplateName(entityFile.getFileType()));
        builtInFiles.add(entityFile);
        //Mapper xml
        OutputFileInfo mapperXmlFile = new OutputFileInfo();
        mapperXmlFile.setBuiltIn(true);
        mapperXmlFile.setFileType(FILE_TYPE_MAPPER_XML);
        mapperXmlFile.setOutputLocation(pathResolver.resolveMapperXmlPackage());
        mapperXmlFile.setTemplateName(templateService.fileType2TemplateName(mapperXmlFile.getFileType()));
        builtInFiles.add(mapperXmlFile);
        //Mapper
        OutputFileInfo mapperFile = new OutputFileInfo();
        mapperFile.setBuiltIn(true);
        mapperFile.setFileType(FILE_TYPE_MAPPER);
        mapperFile.setOutputLocation(pathResolver.resolveMapperPackage());
        mapperFile.setTemplateName(templateService.fileType2TemplateName(mapperFile.getFileType()));
        builtInFiles.add(mapperFile);
        //Dao
        OutputFileInfo daoFile = new OutputFileInfo();
        daoFile.setBuiltIn(true);
        daoFile.setFileType(FILE_TYPE_DAO);
        daoFile.setOutputLocation(pathResolver.resolveDaoPackage());
        daoFile.setTemplateName(templateService.fileType2TemplateName(daoFile.getFileType()));
        builtInFiles.add(daoFile);
        //Service
        OutputFileInfo serviceFile = new OutputFileInfo();
        serviceFile.setBuiltIn(true);
        serviceFile.setFileType(FILE_TYPE_SERVICE);
        serviceFile.setOutputLocation(pathResolver.resolveServicePackage());
        serviceFile.setTemplateName(templateService.fileType2TemplateName(serviceFile.getFileType()));
        builtInFiles.add(serviceFile);
        //Service Impl
        OutputFileInfo serviceImplFile = new OutputFileInfo();
        serviceImplFile.setBuiltIn(true);
        serviceImplFile.setFileType(FILE_TYPE_SERVICE_IMPL);
        serviceImplFile.setOutputLocation(pathResolver.resolveServiceImplPackage());
        serviceImplFile.setTemplateName(templateService.fileType2TemplateName(serviceImplFile.getFileType()));
        builtInFiles.add(serviceImplFile);
        //Controller
        OutputFileInfo controllerFile = new OutputFileInfo();
        controllerFile.setBuiltIn(true);
        controllerFile.setFileType(FILE_TYPE_CONTROLLER);
        controllerFile.setOutputLocation(pathResolver.resolveControllerPackage());
        controllerFile.setTemplateName(templateService.fileType2TemplateName(controllerFile.getFileType()));
        builtInFiles.add(controllerFile);
        return builtInFiles;
    }

    public void deleteOutputFileInfo(OutputFileInfo fileInfo) throws IOException {
        if (fileInfo.isBuiltIn()) {
            throw new ServiceException("内置文件配置信息不能删除");
        }
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        List<OutputFileInfo> fileInfos = userConfig.getOutputFiles();
        fileInfos.remove(fileInfo);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveOutputFileInfo(OutputFileInfo saveFileInfo) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        List<OutputFileInfo> fileInfos = userConfig.getOutputFiles();
        //替换原来的配置
        if (saveFileInfo.isBuiltIn()) {
            Collections.replaceAll(fileInfos, saveFileInfo, saveFileInfo);
        } else if (fileInfos.contains(saveFileInfo)) {
            Collections.replaceAll(fileInfos, saveFileInfo, saveFileInfo);
        } else {
            fileInfos.add(saveFileInfo);
        }
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveEntityStrategy(EntityStrategy entityStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setEntityStrategy(entityStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveMapperXmlStrategy(MapperXmlStrategy mapperXmlStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setMapperXmlStrategy(mapperXmlStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveMapperStrategy(MapperStrategy mapperStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setMapperStrategy(mapperStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveControllerStrategy(ControllerStrategy controllerStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setControllerStrategy(controllerStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveServiceStrategy(ServiceStrategy serviceStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setServiceStrategy(serviceStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public void saveServiceImplStrategy(ServiceImplStrategy serviceImplStrategy) throws IOException {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        userConfig.setServiceImplStrategy(serviceImplStrategy);
        userConfigStore.saveUserConfig(userConfig);
    }

    public String getOutputPkgByFileType(String fileType) {
        UserConfig userConfig = userConfigStore.getDefaultUserConfig();
        List<OutputFileInfo> fileInfos = userConfig.getOutputFiles();
        for (OutputFileInfo fileInfo : fileInfos) {
            if (fileInfo.getFileType().equals(fileType)) {
                return fileInfo.getOutputPackage();
            }
        }
        return null;
    }

}
