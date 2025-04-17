package io.extact.msa.spring.rms.remote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;
import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.rms.remote.resources.controller.WebApiConfig;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepositoryConfig;

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
}
