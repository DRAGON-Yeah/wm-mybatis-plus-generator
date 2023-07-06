/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.wm.mybatis.plus.generator.config.converts;

import com.wm.mybatis.plus.generator.config.GlobalConfig;
import com.wm.mybatis.plus.generator.config.ITypeConvert;
import com.wm.mybatis.plus.generator.config.rules.IColumnType;

import static com.wm.mybatis.plus.generator.config.converts.TypeConverts.contains;
import static com.wm.mybatis.plus.generator.config.converts.TypeConverts.containsAny;
import static com.wm.mybatis.plus.generator.config.rules.DbColumnType.*;

/**
 * SQLServer 字段类型转换
 *
 * @author hubin, hanchunlin
 * @since 2017-01-20
 */
public class SqlServerTypeConvert implements ITypeConvert {

    public static final SqlServerTypeConvert INSTANCE = new SqlServerTypeConvert();

    /**
     * @inheritDoc
     */
    @Override
    public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
        return TypeConverts.use(fieldType)
            .test(containsAny("char", "xml", "text").then(STRING))
            .test(contains("bigint").then(LONG))
            .test(contains("int").then(INTEGER))
            .test(containsAny("date", "time").then(t -> toDateType(config, t)))
            .test(contains("bit").then(BOOLEAN))
            .test(containsAny("decimal", "numeric").then(DOUBLE))
            .test(contains("money").then(BIG_DECIMAL))
            .test(containsAny("binary", "image").then(BYTE_ARRAY))
            .test(containsAny("float", "real").then(FLOAT))
            .or(STRING);
    }

    /**
     * 转换为日期类型
     *
     * @param config 配置信息
     * @param type   类型
     * @return 返回对应的列类型
     */
    public static IColumnType toDateType(GlobalConfig config, String type) {
        switch (config.getDateType()) {
            case SQL_PACK:
                switch (type) {
                    case "date":
                        return DATE_SQL;
                    case "time":
                        return TIME;
                    default:
                        return TIMESTAMP;
                }
            case TIME_PACK:
                switch (type) {
                    case "date":
                        return LOCAL_DATE;
                    case "time":
                        return LOCAL_TIME;
                    default:
                        return LOCAL_DATE_TIME;
                }
            default:
                return DATE;
        }
    }
}
