package com.nowto.springboot.starter.codeenum;

import com.google.common.base.Function;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.transform;
import static springfox.documentation.service.Parameter.DEFAULT_PRECEDENCE;

@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class EnumExpandedParameterBuilderPlugin implements ExpandedParameterBuilderPlugin {

    public EnumExpandedParameterBuilderPlugin() {
    }

    @Override
    public void apply(ParameterExpansionContext context) {
        Class<?> erasedType = context.getFieldType().getErasedType();
        if (!(Enum.class.isAssignableFrom(erasedType) && BaseCodeEnum.class.isAssignableFrom(erasedType))) {
            return;
        }
        ParameterBuilder parameterBuilder = context.getParameterBuilder();

        AllowableValues allowable = allowableValues(erasedType);
        String description = description(erasedType);
        Field descField = ReflectionUtils.findField(parameterBuilder.getClass(), "description");
        ReflectionUtils.makeAccessible(descField);
        description = ReflectionUtils.getField(descField, parameterBuilder)
                + description;

        String name = isNullOrEmpty(context.getParentName())
                ? context.getFieldName()
                : String.format("%s.%s", context.getParentName(), context.getFieldName());

        String typeName = "int";
        ModelReference itemModel = null;
        parameterBuilder
                .name(name)
                .description(description)
                .defaultValue(null)
                .modelRef(new ModelRef(typeName, itemModel))
                .allowableValues(allowable)
                .parameterType(context.getParameterType())
                .order(DEFAULT_PRECEDENCE)
                .parameterAccess(null);
    }

    private String description(Class<?> subject) {
        return Arrays.stream(subject.getEnumConstants())
                .map(input -> ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getCodeMethod, input) + ":" + ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getMeaningMethod, input))
                .collect(Collectors.joining("; ", "(", ")"));
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private AllowableValues allowableValues(Class<?> fieldType) {
        return new AllowableListValues(getEnumValues(fieldType), "LIST");
    }

    private List<String> getEnumValues(final Class<?> subject) {
        return transform(Arrays.asList(subject.getEnumConstants()),
                (Function<Object, String>) input -> String.valueOf(ReflectionUtils.invokeMethod(EnumModelPropertyBuilderPlugin.getCodeMethod, input)));
    }
}