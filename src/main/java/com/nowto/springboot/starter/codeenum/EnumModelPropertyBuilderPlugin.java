package com.nowto.springboot.starter.codeenum;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnumModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {
    public static final Method getCodeMethod;
    public static final Method getMeaningMethod;
    static {
        try {
            getCodeMethod = BaseCodeEnum.class.getMethod("getCode");
            getMeaningMethod = BaseCodeEnum.class.getMethod("getMeaning");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("实际不会发生该异常，这里抛异常抑制编译器报错");
        }
    }

    @Override
    public void apply(ModelPropertyContext context) {

        Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
        if (!optional.isPresent()) {
            return;
        }

        final Class<?> fieldType = optional.get().getRawPrimaryType();

        addDescForEnum(context, fieldType);
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private void addDescForEnum(ModelPropertyContext context, Class<?> fieldType) {
        if (Enum.class.isAssignableFrom(fieldType) && BaseCodeEnum.class.isAssignableFrom(fieldType)) {
            Object[] enumConstants = fieldType.getEnumConstants();

            String description =
                    Arrays.stream(enumConstants)
                            .filter(Objects::nonNull)
                            .map(item -> {
                                Object code = ReflectionUtils.invokeMethod(getCodeMethod, item);
                                Object meaning = ReflectionUtils.invokeMethod(getMeaningMethod, item);
                                return code + ":" + meaning;
                            }).collect(Collectors.joining("; ", "(", ")"));

            ModelPropertyBuilder builder = context.getBuilder();
            Field descField = ReflectionUtils.findField(builder.getClass(), "description");
            ReflectionUtils.makeAccessible(descField);
            String joinText = ReflectionUtils.getField(descField, builder)
                    + description;
            builder.description(joinText).type(context.getResolver().resolve(Integer.class));
        }

    }
}