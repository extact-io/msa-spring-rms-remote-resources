package io.extact.msa.spring.rms.remote;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.rms.remote.resources.controller.WebApiConfig;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepositoryConfig;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

@SpringBootConfiguration
@EnableAutoConfigurationWithoutJpa
@Import({
        CoreConfig.class,
        WebApiConfig.class,
        InMemoryRepositoryConfig.class
})
public class RemoteResourcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemoteResourcesApplication.class, args);
    }

    // TODO: Condationalにしてfwに持っていく
    @Bean
    OpenTelemetryAppenderInitializer openTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
        return new OpenTelemetryAppenderInitializer(openTelemetry);
    }

    static class OpenTelemetryAppenderInitializer {

        private final OpenTelemetry openTelemetry;

        OpenTelemetryAppenderInitializer(OpenTelemetry openTelemetry) {
            this.openTelemetry = openTelemetry;
        }

        @PostConstruct
        void init() {
            OpenTelemetryAppender.install(this.openTelemetry);
        }

    }
}
