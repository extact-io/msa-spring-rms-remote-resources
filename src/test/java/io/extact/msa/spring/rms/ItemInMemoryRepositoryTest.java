package io.extact.msa.spring.rms;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepositoryConfig;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles({ "user" })
class ItemInMemoryRepositoryTest {

    @Autowired
    private InMemoryRepository repository;

    @Configuration(proxyBeanMethods = false)
    @Import(InMemoryRepositoryConfig.class)
    static class TestConfig {
    }

    @Test
    void testGetAll() {
        repository.getAll().forEach(System.out::println);
    }
}
