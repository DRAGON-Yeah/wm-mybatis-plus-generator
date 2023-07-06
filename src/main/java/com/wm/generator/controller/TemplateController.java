package com.wm.generator.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.wm.generator.common.Result;
import com.wm.generator.common.ResultGenerator;
import com.wm.generator.common.ServiceException;
import com.wm.generator.dto.OutputFileInfo;
import com.wm.generator.dto.UserConfig;
import com.wm.generator.service.OutputFileInfoService;
import com.wm.generator.service.TemplateService;
import com.wm.generator.service.UserConfigStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/template")
@Slf4j
public class TemplateController {

    @Resource
    private UserConfigStore userConfigStore;

    @Resource
    private TemplateService templateService;

    @Resource
    private OutputFileInfoService outputFileInfoService;

    @GetMapping("/download")
    public void download(HttpServletResponse res, @RequestParam String fileType) throws UnsupportedEncodingException, FileNotFoundException {
        if (Strings.isNullOrEmpty(fileType)) {
            log.error("fileType不能为空");
            return;
        }
        UserConfig userConfig = userConfigStore.getUserConfigFromFile();
        if (userConfig == null) {
            InputStream tplIn = templateService.getBuiltInTemplate(fileType);
            download(res, tplIn);
            return;
        }
        List<OutputFileInfo> fileInfos = userConfig.getOutputFiles();
        for (OutputFileInfo fileInfo : fileInfos) {
            if (fileType.equals(fileInfo.getFileType())) {
                if (fileInfo.isBuiltIn()
                        && Strings.isNullOrEmpty(fileInfo.getTemplatePath())) {
                    InputStream tplIn = templateService.getBuiltInTemplate(fileType);
                    download(res, tplIn);
                } else {
                    String tplPath = fileInfo.getTemplatePath();
                    if (tplPath.startsWith("file:")) {
                        tplPath = tplPath.replaceFirst("file:", "");
                    }
                    File tplFile = new File(tplPath);
                    if (tplFile.exists()) {
                        download(res, new FileInputStream(tplFile));
                    } else {
                        throw new ServiceException("未找到模板文件：" + fileInfo.getTemplatePath());
                    }
                }
                break;
            }
        }
    }

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) {
        Map<String, Object> params = Maps.newHashMap();
        String storePath = userConfigStore.uploadTemplate(file);
        params.put("templatePath", storePath);
        params.put("templateName", file.getOriginalFilename());
        return ResultGenerator.genSuccessResult(params);
    }

    private void download(HttpServletResponse res, InputStream tplIn) throws UnsupportedEncodingException {
        if (tplIn != null) {
            res.setCharacterEncoding("utf-8");
            res.setContentType("multipart/form-data;charset=UTF-8");
            try {
                OutputStream os = res.getOutputStream();
                byte[] b = new byte[2048];
                int length;
                while ((length = tplIn.read(b)) > 0) {
                    os.write(b, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (tplIn != null) {
                    try {
                        tplIn.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


}
