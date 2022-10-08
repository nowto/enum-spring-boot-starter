package com.nowto.springboot.starter.codeenum;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Joiner;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EnumOperationBuilderPlugin implements OperationBuilderPlugin {
    private static final Joiner joiner = Joiner.on(",");

    @Override
    public void apply(OperationContext context) {
        Map<String, String> map = new HashMap<>();
        List<ResolvedMethodParameter> parameters = context.getParameters();
        parameters.forEach(parameter -> {
            ResolvedType parameterType = parameter.getParameterType();
            Class<?> clazz = parameterType.getErasedType();
            if (Enum.class.isAssignableFrom(clazz) && BaseCodeEnum.class.isAssignableFrom(clazz)) {
                Object[] enumConstants = clazz.getEnumConstants();

                String displayValues = Arrays.stream(enumConstants).filter(Objects::nonNull).map(item -> {
                    Object code = ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getCodeMethod, item);
                    Object meaning = ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getMeaningMethod, item);
                    return code + ":" + meaning;

                }).collect(Collectors.joining("; ", "(", ")"));

                map.put(parameter.defaultName().or(""), displayValues);

                OperationBuilder operationBuilder = context.operationBuilder();
                Field parametersField = ReflectionUtils.findField(operationBuilder.getClass(), "parameters");
                ReflectionUtils.makeAccessible(parametersField);
                List<Parameter> list = (List<Parameter>) ReflectionUtils.getField(parametersField, operationBuilder);

                map.forEach((k, v) -> {
                    for (Parameter currentParameter : list) {
                        if (currentParameter.getName() != null && currentParameter.getName().equals(k)) {
                            Field description = ReflectionUtils.findField(currentParameter.getClass(), "description");
                            ReflectionUtils.makeAccessible(description);
                            Object field = ReflectionUtils.getField(description, currentParameter);
                            ReflectionUtils.setField(description, currentParameter, field  + v);
                            break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}