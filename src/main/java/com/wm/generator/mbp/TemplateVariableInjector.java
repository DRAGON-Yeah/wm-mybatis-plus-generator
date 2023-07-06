package com.wm.generator.mbp;

import com.wm.mybatis.plus.generator.config.po.TableInfo;

import java.util.Map;

public interface TemplateVariableInjector {


    Map<String, Object> getCustomTemplateVariables(TableInfo tableInfo);


}
