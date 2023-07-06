package com.wm.generator.strategy;

import com.wm.mybatis.plus.generator.config.ConstVal;
import lombok.Data;

@Data
public class DaoStrategy {

    /**
     * 自定义继承的Dao类全称，带包名
     */
    private String superServiceClass = ConstVal.SUPER_DAO_CLASS;

    
}
