package com.wm.generator.controller;

import com.wm.generator.common.Result;
import com.wm.generator.common.ResultGenerator;
import com.wm.generator.dto.MpgGenCodeDto;
import com.wm.generator.service.MbpGeneratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/mbp-generator")
public class MbpGeneratorController {

    @Resource
    private MbpGeneratorService mbpGeneratorService;

    @PostMapping("/gen-code")
    public Result genCode(@RequestBody MpgGenCodeDto dto) {
        mbpGeneratorService.genCodeBatch(dto.getGenSetting(), dto.getTables());
        return ResultGenerator.genSuccessResult();
    }

}
