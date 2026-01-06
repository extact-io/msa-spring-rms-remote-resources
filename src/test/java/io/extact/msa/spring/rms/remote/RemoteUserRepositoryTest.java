package io.extact.msa.spring.rms.remote;

import static io.extact.msa.spring.test.junit5.Constants.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.test.utils.TestAuthUtils;
import io.extact.msa.spring.rms.remote.client.UserResourceClient;
import io.extact.msa.spring.rms.remote.resources.UserResource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "user", "test" })
@TestMethodOrder(OrderAnnotation.class)
class RemoteUserRepositoryTest {

    private static final UserResource user1 = //
            new UserResource(
                    1,
                    "member1",
                    "member1",
                    "メンバー1",
                    "070-1111-2222",
                    "連絡先1",
                    "MEMBER");
    private static final UserResource user2 = //
            new UserResource(
                    2,
                    "member2",
                    "member2",
                    "メンバー2",
                    "080-1111-2222",
                    "連絡先2",
                    "MEMBER");
    private static final UserResource user3 = //
            new UserResource(
                    3,
                    "admin",
                    "admin",
                    "管理者",
                    "050-1111-2222",
                    "連絡先3",
                    "ADMIN");

    @Autowired
    private UserResourceClient client;

    @Configuration(proxyBeanMethods = false)
    @Import(RemoteResourcesApplication.class)
    static class TestConfig {

        @Bean
        @ConfigurationProperties("rms.persistence.user.remote")
        ExternalProperties externalProperties() {
            return new ExternalProperties();
        }

        @Bean
        UserResourceClient userResourceClient(ExternalProperties prop, Environment env) {
            return ClientFactoryUtils.createClient(prop, env, UserResourceClient.class);
        }
    }

    @BeforeEach
    void beforeEach() {
        TestAuthUtils.signinByHeader(1, "MEMBER");
    }

    @AfterEach
    void afterEach() {
        TestAuthUtils.signoutQuietly();
    }

    @Test
    void testGet() {

        // given
        Integer userId = 1;
        // when
        UserResource actual = client.get(userId);
        // then
        assertThat(actual).isNotNull().isEqualTo(user1);

        // given
        Integer notFoundId = 999;
        // when
        actual = client.get(notFoundId);
        // then
        assertThat(actual).isNull();
    }

    @Test
    void testGetAll() {
        // given
        List<UserResource> expected = List.of(user1, user2, user3);
        // when
        Collection<UserResource> actual = client.getAll();
        // then
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testUpdate() {
        // given
        UserResource updateUser = new UserResource(
                1,
                "update1",
                "update1",
                "ADMIN",
                "メンバーUP",
                "070-1111-9999",
                "連絡先UP");
        // when
        boolean result = client.update(updateUser);
        //then
        assertThat(result).isTrue();
        assertThat(client.get(updateUser.id())).isEqualTo(updateUser);
    }

    @Test
    void testUpdateOnNotFound() {
        // given
        UserResource notFoundUser = new UserResource(
                999,
                "update1",
                "update1",
                "ADMIN",
                "メンバーUP",
                "070-1111-9999",
                "連絡先UP");
        // when
        boolean result = client.update(notFoundUser);
        // then
        assertThat(result).isFalse();
        assertThat(client.get(notFoundUser.id())).isNull();
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testAdd() {
        // given
        UserResource addUser = new UserResource(
                4,
                "add-1",
                "add-1",
                "MEMBER",
                "メンバーADD",
                "070-1111-8888",
                "連絡先ADD");
        // when
        client.add(addUser);
        // then
        assertThat(client.get(addUser.id())).isNotNull().isEqualTo(addUser);
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testDelete() {
        // given
        int deleteId = 3;
        // when
        boolean result = client.delete(deleteId);
        // then
        assertThat(result).isTrue();
        assertThat(client.get(deleteId)).isNull();
    }

    @Test
    void testDeleteOnNotFound() {
        // given
        int notFoundId = 999;
        // when
        boolean result = client.delete(notFoundId);
        // then
        assertThat(result).isFalse();
        assertThat(client.get(notFoundId)).isNull();
    }

    @Test
    void testNextIdentity() {
        // given
        int currentId = client.getAll().size();
        // when
        int nextId = client.nextIdentity();
        // then
        assertThat(nextId).isEqualTo(currentId + 1);
    }

    @Test
    void testFindDpulicationData() {

        // given
        UserResource foundUser = user1;
        // when
        UserResource result = client.findByLoginId(foundUser.loginId());
        // then
        assertThat(result).isEqualTo(result);

        // given
        String notFoundLoginId = "xyz";
        // when
        result = client.findByLoginId(notFoundLoginId);
        // then
        assertThat(result).isNull();
    }

    @Test
    void testFindByLoginIdAndPassword() {

        // given
        String loginId = user1.loginId();
        String password = user1.password();
        // when
        UserResource result = client.findByLoginIdAndPassword(loginId, password);
        // then
        assertThat(result).isEqualTo(user1);

        // given
        loginId = "unknown";
        password = "unknown";
        // when
        result = client.findByLoginIdAndPassword(loginId, password);
        // then
        assertThat(result).isNull();
    }
}
