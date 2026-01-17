package io.extact.msa.spring.rms.remote;

import static io.extact.msa.spring.test.junit5.Constants.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SingleRestClientConfig;
import io.extact.msa.spring.platform.fw.test.customizer.LocalHostUriBuilderFactoryCustomizer;
import io.extact.msa.spring.platform.fw.test.utils.TestAuthUtils;
import io.extact.msa.spring.rms.remote.client.ReservationResourceClient;
import io.extact.msa.spring.rms.remote.resources.ReservationResource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "reservation", "test" })
@TestMethodOrder(OrderAnnotation.class)
class RemoteReservationRepositoryTest {

    private static final ReservationResource reservation1 = //
            new ReservationResource(
                    1,
                    LocalDateTime.of(2020, 4, 1, 10, 0),
                    LocalDateTime.of(2020, 4, 1, 12, 0),
                    "メモ1",
                    3,
                    1);
    private static final ReservationResource reservation2 = //
            new ReservationResource(
                    2,
                    LocalDateTime.of(2020, 4, 1, 16, 0),
                    LocalDateTime.of(2020, 4, 1, 18, 0),
                    "メモ2",
                    3,
                    2);
    private static final ReservationResource reservation3 = //
            new ReservationResource(
                    3,
                    LocalDateTime.of(2099, 4, 1, 10, 0),
                    LocalDateTime.of(2099, 4, 1, 12, 0),
                    "メモ3",
                    3,
                    1);

    @Autowired
    private ReservationResourceClient client;

    @Configuration(proxyBeanMethods = false)
    @Import({
        RemoteResourcesApplication.class,
        SingleRestClientConfig.class })
    static class TestConfig {

        @Bean
        @ConfigurationProperties("rms.persistence.reservation.remote")
        ExternalProperties externalProperties() {
            return new ExternalProperties();
        }

        @Bean
        RmsRestClientCustomizer overrideRestClientConfig() {
            return LocalHostUriBuilderFactoryCustomizer.INSTANCE;
        }

        @Bean
        ReservationResourceClient userResourceClient(HttpServiceProxyFactory factory) {
            return factory.createClient(ReservationResourceClient.class);
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
        Integer foundId = 1;
        // when
        ReservationResource actual = client.get(foundId);
        // then
        assertThat(actual).isNotNull().isEqualTo(reservation1);

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
        List<ReservationResource> expected = List.of(reservation1, reservation2, reservation3);
        // when
        Collection<ReservationResource> actual = client.getAll();
        // then
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testUpdate() {
        // given
        ReservationResource updateReservation = //
                new ReservationResource(
                        1,
                        LocalDateTime.of(2024, 10, 30, 10, 0),
                        LocalDateTime.of(2025, 11, 1, 12, 0),
                        "UPDATE",
                        1,
                        3);
        // when
        boolean result = client.update(updateReservation);
        //then
        assertThat(result).isTrue();
        assertThat(client.get(updateReservation.id())).isEqualTo(updateReservation);
    }

    @Test
    void testUpdateOnNotFound() {
        // given
        ReservationResource notFoundReservation = //
                new ReservationResource(
                        999,
                        LocalDateTime.of(2024, 10, 30, 10, 0),
                        LocalDateTime.of(2025, 11, 1, 12, 0),
                        "UPDATE",
                        1,
                        3);

        // when
        boolean result = client.update(notFoundReservation);
        // then
        assertThat(result).isFalse();
        assertThat(client.get(notFoundReservation.id())).isNull();
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testAdd() {

        // given
        LocalDateTime from = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = from.plusHours(2);

        ReservationResource addReservation = //
                new ReservationResource(
                        4,
                        from,
                        to,
                        "ADD",
                        1,
                        3);
        // when
        client.add(addReservation);
        // then
        assertThat(client.get(addReservation.id())).isNotNull().isEqualTo(addReservation);
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
    void testFindByCondition() {

        // ---- 3件ヒット
        // given
        Integer itemId = 3;
        Integer reserverId = null;
        LocalDate from = null;
        // when
        List<ReservationResource> actual = client.findByCondition(itemId, reserverId, from);
        // then
        List<ReservationResource> expected = List.of(reservation1, reservation2, reservation3);
        assertThat(actual).containsExactlyElementsOf(expected);

        // ---- 1件ヒット
        // given
        itemId = 3;
        reserverId = 1;
        from = LocalDate.of(2020, 4, 1);
        // when
        actual = client.findByCondition(itemId, reserverId, from);
        // then
        expected = List.of(reservation1);
        assertThat(actual).containsExactlyElementsOf(expected);

        // ---- 0件ヒット
        // given
        itemId = 1;
        reserverId = 1;
        from = LocalDate.of(2020, 4, 1);
        // when
        actual = client.findByCondition(itemId, reserverId, from);
        // then
        assertThat(actual).isEmpty();
    }
}
