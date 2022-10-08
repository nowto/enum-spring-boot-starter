package com.nowto.springboot.starter.codeenum;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 代表了一个数字与语义相关的枚举接口.
 * 主要用于数据库存储使用，数据库不实际存储字段的实际语义，而是存储数字代表其实际的语义，比如：性别字段：
 * 1-男， 2-女。
 * 该接口的实现类应该是{@link Enum}类， 当用于domain类的字段类型时，这样的实现类可以被mybatis接口的
 * {@link CodeEnumTypeHandler}实现所处理
 * @see CodeEnumTypeHandler
 * @author liweibo
 */
public interface BaseCodeEnum <T extends Enum<T> & BaseCodeEnum> {
    /**
     * 获取该枚举值的数值代码
     * @return 数值代码
     */
    @JsonValue
    int getCode();

    /**
     * 获取其该枚举值其实际的意义
     * @return 实际意义
     */
    String getMeaning();
}