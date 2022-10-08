package com.nowto.springboot.starter.codeenum;

import com.google.common.base.Joiner;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnumParameterBuilderPlugin implements ParameterBuilderPlugin {
    private static final Joiner joiner = Joiner.on(",");

    @Override
    public void apply(ParameterContext context) {
        Class<?> type = context.resolvedMethodParameter().getParameterType().getErasedType();
        if (Enum.class.isAssignableFrom(type) && BaseCodeEnum.class.isAssignableFrom(type)) {
            Object[] enumConstants = type.getEnumConstants();
            List<String> displayValues = Arrays.stream(enumConstants).filter(Objects::nonNull).map(item -> {
                Object code = ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getCodeMethod, item);
                return code.toString();

            }).collect(Collectors.toList());

            ParameterBuilder parameterBuilder = context.parameterBuilder();
            AllowableListValues values = new AllowableListValues(displayValues, "LIST");
            parameterBuilder.allowableValues(values);
        }
    }


    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}