package com.nowto.springboot.starter.codeenum;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对{@link BaseCodeEnum}操作的相关工具方法
 * @author liweibo
 */
public class CodeEnumUtil {
    private static final ConcurrentHashMap<CacheKey, BaseCodeEnum<?>> cache = new ConcurrentHashMap<>();



    private CodeEnumUtil() {
        // 工具类， 拒绝实例化
    }

    /**
     * 返回枚举类codeEnumClass的枚举值中code值为code的枚举常量，如果没有，返回null
     * @param codeEnumClass 枚举类
     * @param code code
     * @param <E> 枚举类型
     * @return 枚举值为code的枚举常量，如果找不到返回null
     */
    public static <E extends Enum & BaseCodeEnum> E codeOf(Class<E> codeEnumClass, int code) {
        return codeOf(codeEnumClass, code, null);
    }

    /**
     * 返回枚举类codeEnumClass的枚举值中code值为code的枚举常量，如果没有，返回null
     * @param codeEnumClass 枚举类
     * @param code code
     * @param <E> 枚举类型
     * @return 枚举值为code的枚举常量，如果找不到返回null
     */
    public static <E extends Enum & BaseCodeEnum> E codeOf(Class<E> codeEnumClass, int code, E defaultValue) {
        Assert.notNull(codeEnumClass, "codeEnumClass不能为null");
        BaseCodeEnum<?> value = cache.computeIfAbsent(new CacheKey(codeEnumClass, code), key -> {
            E[] enumConstants = codeEnumClass.getEnumConstants();
            for (E e : enumConstants) {
                if (e.getCode() == code) {
                    return e;
                }
            }
            return null;
        });
        return value == null ? defaultValue : (E) value;
    }

    private static class CacheKey<E extends Enum & BaseCodeEnum> {
        private Class<E> codeEnumClass;
        private int code;

        public CacheKey(Class<E> codeEnumClass, int code) {
            this.codeEnumClass = codeEnumClass;
            this.code = code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheKey<?> cacheKey = (CacheKey<?>) o;
            return code == cacheKey.code && Objects.equals(codeEnumClass, cacheKey.codeEnumClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codeEnumClass, code);
        }
    }
}