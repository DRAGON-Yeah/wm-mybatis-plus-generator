package com.wm.mybatis.plus.generator;


import java.util.List;
import java.util.Map;

import com.wm.mybatis.plus.generator.config.FileOutConfig;
import com.wm.mybatis.plus.generator.config.IFileCreate;
import com.wm.mybatis.plus.generator.config.builder.ConfigBuilder;

import com.wm.mybatis.plus.generator.config.po.TableInfo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 抽象的对外接口
 *
 * @author hubin
 * @since 2016-12-07
 */
@Data
@Accessors(chain = true)
public abstract class InjectionConfig {

    /**
     * 全局配置
     */
    private ConfigBuilder config;

    /**
     * 自定义返回配置 Map 对象
     */
    private Map<String, Object> map;

    /**
     * 自定义输出文件
     */
    private List<FileOutConfig> fileOutConfigList;

    /**
     * 自定义判断是否创建文件
     */
    private IFileCreate fileCreate;

    /**
     * 注入自定义 Map 对象，针对所有表的全局参数
     */
    public abstract void initMap();

    /**
     * 依据表相关信息，从三方获取到需要元数据，处理方法环境里面
     *
     * @param tableInfo
     */
    public void initTableMap(TableInfo tableInfo) {
        // 子类重写注入表对应补充信息
    }

    /**
     * 模板待渲染 Object Map 预处理<br>
     * com.wm.mybatis.plus.generator.engine.AbstractTemplateEngine
     * 方法： getObjectMap 结果处理
     */
    public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
        return objectMap;
    }
}
