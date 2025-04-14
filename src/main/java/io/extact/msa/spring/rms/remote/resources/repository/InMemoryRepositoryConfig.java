package io.extact.msa.spring.rms.remote.resources.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
public class InMemoryRepositoryConfig {

    @Bean
    @Profile("item")
    ItemInMemoryRepository itemInMemoryRepository() {
        return new ItemInMemoryRepository();
    }

    @Bean
    @Profile("reservation")
    ReservationInMemoryRepository reservationInMemoryRepository() {
        return new ReservationInMemoryRepository();
    }

    @Bean
    @Profile("user")
    UserInMemoryRepository userInMemoryRepository() {
        return new UserInMemoryRepository();
    }
}
