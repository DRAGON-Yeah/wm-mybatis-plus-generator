package com.wm.generator.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.wm.generator.common.ServiceException;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.dto.UserConfig;
import com.wm.generator.util.JsonUtil;
import com.wm.generator.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.wm.generator.dto.Constant.*;

@Component
@Slf4j
public class UserConfigStore {

    private String storeDir;

    private String userConfigPath;

    @Resource
    private OutputFileInfoService outputFileInfoService;

    @Resource
    private GeneratorConfig generatorConfig;

    @PostConstruct
    public void init() {
        this.storeDir = PathUtil.joinPath(CONFIG_HOME, TEMPLATE_STORE_DIR, generatorConfig.getBasePackage());
        this.userConfigPath = this.storeDir + File.separator + "user-config.json";
    }

    public String getTemplateStoreDir() {
        return this.storeDir;
    }

    public UserConfig getDefaultUserConfig() {
        UserConfig userConfig = getUserConfigFromFile();
        if (userConfig == null) {
            userConfig = new UserConfig();
            userConfig.setOutputFiles(outputFileInfoService.getBuiltInFileInfo());
        }
        return userConfig;
    }

    public UserConfig getUserConfigFromFile() {
        if (!FileUtil.exist(this.userConfigPath)) {
            return null;
        }
        String userConfigStr = FileUtil.readString(userConfigPath, Charset.forName("utf-8"));
        try {
            return JsonUtil.json2obj(userConfigStr, UserConfig.class);
        } catch (Exception e) {
            log.error("读取用户配置文件发生错误：", e);
            return null;
        }
    }

    public void saveUserConfig(UserConfig userConfig) throws IOException {
        if (userConfig == null) {
            throw new ServiceException("不能写入空的用户配置");
        }
        String configStr = JsonUtil.obj2json(userConfig);
        File userConfigFile = new File(this.userConfigPath);
        if (userConfigFile.exists()) {
            userConfigFile.delete();
        }
        Files.createParentDirs(userConfigFile);
        userConfigFile.createNewFile();
        FileUtil.writeFromStream(new ByteArrayInputStream(configStr.getBytes(Charset.forName("utf-8"))), userConfigFile);
    }

    public String uploadTemplate(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String saveFileName = fileName.substring(0, fileName.lastIndexOf(fileSuffix)) + DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String savePath = PathUtil.joinPath(getTemplateStoreDir(), saveFileName);
        log.info("模板上传路径为：{}", savePath);
        File saveFile = new File(savePath);
        try {
            FileUtil.writeFromStream(file.getInputStream(), saveFile);
        } catch (IOException e) {
            throw new ServiceException("上传模板文件失败", e);
        }
        return RESOURCE_PREFIX_FILE + savePath;
    }

    public boolean checkUserConfigExisted() {
        if (!FileUtil.exist(this.storeDir)) {
            return false;
        }
        return true;
    }

    public void importProjectConfig(String sourcePkg) throws IOException {
        String configHomePath = PathUtil.joinPath(System.getProperty("user.home"), CONFIG_HOME);
        if (!FileUtil.exist(configHomePath)) {
            throw new ServiceException("配置主目录不存在：" + configHomePath);
        }
        File[] files = FileUtil.ls(configHomePath);
        boolean flag = false;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(sourcePkg)) {
                File projectConfigDir = new File(this.storeDir);
                FileUtil.copyContent(file, projectConfigDir, true);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ServiceException("未找到待导入的源项目配置");
        }
        String sourceProjectConfigPath = PathUtil.joinPath(System.getProperty("user.home"), CONFIG_HOME, sourcePkg);
        String targetProjectConfigPath = this.storeDir;
        UserConfig currentUserConfig = new UserConfig();
        currentUserConfig.setOutputFiles(outputFileInfoService.getBuiltInFileInfo());
        currentUserConfig.merge(this.getUserConfigFromFile(), sourceProjectConfigPath, targetProjectConfigPath);
        this.saveUserConfig(currentUserConfig);
    }

    public List<String> getAllSavedProject() {
        String configHomePath = PathUtil.joinPath(System.getProperty("user.home"), CONFIG_HOME);
        if (!FileUtil.exist(configHomePath)) {
            return Collections.emptyList();
        }
        List<String> projects = Lists.newArrayList();
        File[] files = FileUtil.ls(configHomePath);
        for (File file : files) {
            if (file.isDirectory()) {
                projects.add(file.getName());
            }
        }
        return projects;
    }

}
