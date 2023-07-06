package com.wm.generator.controller;

import com.wm.generator.common.Result;
import com.wm.generator.common.ResultGenerator;
import com.wm.generator.config.ProjectPathResolver;
import com.wm.generator.service.AutoCompleteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@RestController
@RequestMapping("/api/ac")
public class AutoCompleteController {

    @Resource
    private AutoCompleteService autoCompleteService;

    @Resource
    private ProjectPathResolver projectPathResolver;

    @GetMapping("/mapperxml")
    public Result getAllMapperXmlNames(String mapperLocationPrefix, String searchKey) {
        Set<String> hits = autoCompleteService.searchXmlMapperName(mapperLocationPrefix, searchKey);
        return ResultGenerator.genSuccessResult(hits);
    }

}
