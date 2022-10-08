package com.nowto.springboot.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowto.springboot.starter.codeenum.*;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.Servlet;

/**
 * 自动配置类
 * @author liweibo
 */
@Configuration
public class EnumAutoConfiguration {
    /**
     * 向Mybatis注册TypeHandler：CodeEnumTypeHandler
     */
    @Configuration
    @ConditionalOnClass({MybatisAutoConfiguration.class, org.apache.ibatis.session.SqlSession.class})
    @AutoConfigureBefore(MybatisAutoConfiguration.class)
    public static class MybatisConfiguration {

        @Bean
        public ConfigurationCustomizer codeEnumTypeHandlerCustomizer() {
            return new CodeEnumTypeHandlerCustomizer();
        }

        public static class CodeEnumTypeHandlerCustomizer implements ConfigurationCustomizer{
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.getTypeHandlerRegistry().register(CodeEnumTypeHandler.class);
            }
        }
    }


    /**
     * Spring MVC自动注册.
     * 注册Converter：{@link StringToBaseCodeEnumConverterFactory},
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class, FormatterRegistry.class, WebMvcAutoConfiguration.class})
    @AutoConfigureBefore(WebMvcAutoConfiguration.class)
    public static class MvcConfiguration  implements WebMvcConfigurer {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverterFactory(new StringToBaseCodeEnumConverterFactory<>());
        }
    }

    @Configuration
    @ConditionalOnClass({DocumentationType.class, ObjectMapper.class})
    public static class SwaggerConfiguration {
        @Bean
        public EnumModelPropertyBuilderPlugin enumModelPropertyBuilderPlugin() {
            return new EnumModelPropertyBuilderPlugin();
        }

        @Bean
        public EnumParameterBuilderPlugin enumParameterBuilderPlugin() {
            return new EnumParameterBuilderPlugin();
        }

        @Bean
        public EnumOperationBuilderPlugin enumOperationBuilderPlugin() {
            return new EnumOperationBuilderPlugin();
        }

        @Bean
        public EnumExpandedParameterBuilderPlugin enumExpandedParameterBuilderPlugin() {
            return new EnumExpandedParameterBuilderPlugin();
        }
    }
}