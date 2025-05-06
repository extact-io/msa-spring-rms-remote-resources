package io.extact.msa.spring.rms.remote.resources.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.auth.header.RmsHeaderAuthConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.rms.remote.resources.repository.ItemInMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.ReservationInMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.UserInMemoryRepository;

@Configuration(proxyBeanMethods = false)
@Import({
        RmsHeaderAuthConfig.class,
        RestControllerConfig.class
})
public class WebApiConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/remote",
                HandlerTypePredicate.forAssignableType(
                        ItemResourceController.class,
                        ReservationResourceController.class,
                        UserResourceController.class));
    }

    @Bean
    AuthorizeHttpRequestCustomizer authorizeRequestCustomizer() {
        return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                .requestMatchers("/remote/*/reset").hasRole("SYSTEM")
                .requestMatchers("/remote/users/auth").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    @Profile("item")
    ItemResourceController itemResourceController(ItemInMemoryRepository repository) {
        return new ItemResourceController(repository);
    }

    @Bean
    @Profile("reservation")
    ReservationResourceController reservationResourceController(ReservationInMemoryRepository repository) {
        return new ReservationResourceController(repository);
    }

    @Bean
    @Profile("user")
    UserResourceController userResourceController(UserInMemoryRepository repository) {
        return new UserResourceController(repository);
    }
}
