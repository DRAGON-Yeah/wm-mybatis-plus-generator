package com.wm.generator.controller;

import com.wm.generator.common.Result;
import com.wm.generator.common.ResultGenerator;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.dto.GenDtoFromSqlReq;
import com.wm.generator.service.SqlGeneratorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/sql")
public class SqlGeneratorController {

    @Resource
    private SqlGeneratorService sqlGeneratorService;

    @Resource
    private GeneratorConfig generatorConfig;

    @GetMapping("/basepackage")
    public Result getBasepackage() {
        return ResultGenerator.genSuccessResult(generatorConfig.getBasePackage());
    }


    @PostMapping("/gen-mapper-method")
    public Result genMapperMethodFromSQL(@RequestBody GenDtoFromSqlReq params) throws Exception {
        sqlGeneratorService.genMapperMethod(params);
        return ResultGenerator.genSuccessResult();
    }


}
