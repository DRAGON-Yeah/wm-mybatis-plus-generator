package com.wm.generator.mbp;

import com.google.common.collect.Maps;
import com.wm.generator.config.GeneratorConfig;
import com.wm.generator.dto.GenSetting;
import com.wm.mybatis.plus.generator.InjectionConfig;
import com.wm.mybatis.plus.generator.config.po.TableInfo;

import java.util.Map;

public class TableInjectionConfig extends InjectionConfig {

    private GeneratorConfig generatorConfig;

    private TemplateVariableInjector variableInjector;

    private GenSetting genSetting;

    public TableInjectionConfig(GeneratorConfig generatorConfig, GenSetting genSetting) {
        this.generatorConfig = generatorConfig;
        this.variableInjector = generatorConfig.getTemplateVariableInjector();
        this.genSetting = genSetting;
    }

    @Override
    public void initMap() {
    }

    @Override
    public void initTableMap(TableInfo tableInfo) {
        Map<String, Object> vars = null;
        if (this.variableInjector != null) {
            vars = this.variableInjector.getCustomTemplateVariables(tableInfo);
        }
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        if (genSetting.getChoosedControllerMethods() != null) {
            Map<String, Object> controllerMethodsVar = Maps.newHashMap();
            for (String method : genSetting.getChoosedControllerMethods()) {
                controllerMethodsVar.put(method, true);
            }
            if (controllerMethodsVar.size() > 0) {
                controllerMethodsVar.put("hasMethod", true);
            }
            vars.put("controllerMethods", controllerMethodsVar);
        }
        this.setMap(vars);
    }

}
