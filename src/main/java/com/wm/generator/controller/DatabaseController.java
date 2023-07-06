package com.wm.generator.controller;

import com.wm.generator.common.Result;
import com.wm.generator.common.ResultGenerator;
import com.wm.generator.dto.TableInfo;
import com.wm.generator.service.DatabaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @Resource
    private DatabaseService databaseService;

    @GetMapping("/tables")
    public Result getAllTables() {
        List<TableInfo> tables = databaseService.getTablesFromDb();
        return ResultGenerator.genSuccessResult(tables);
    }

}
