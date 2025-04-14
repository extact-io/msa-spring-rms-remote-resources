package io.extact.msa.spring.rms.remote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfig;

@SpringBootConfiguration
@Import(CoreConfig.class)
public class RemoteResourcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemoteResourcesApplication.class, args);
    }
}
