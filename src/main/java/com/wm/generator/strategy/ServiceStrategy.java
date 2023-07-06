package com.wm.generator.strategy;

import com.wm.mybatis.plus.generator.config.ConstVal;
import lombok.Data;

@Data
public class ServiceStrategy {

    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass = ConstVal.SUPER_SERVICE_CLASS;

    
}
