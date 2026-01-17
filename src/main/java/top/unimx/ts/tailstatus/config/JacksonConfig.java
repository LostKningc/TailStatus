package top.unimx.ts.tailstatus.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 1. 容错设置：遇到 DTO 里没有定义的字段，直接忽略，不要报错
            // 这对于消费第三方 API 非常重要，防止 API 升级导致我们挂掉
            builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            // 2. 时间模块：支持 Java 8 的 LocalDateTime / ZonedDateTime
            // Tailscale 的时间字段是 ISO 格式，这个模块能自动处理
            builder.modules(new JavaTimeModule());
        };
    }
}
