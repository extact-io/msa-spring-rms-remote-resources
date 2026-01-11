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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.test.utils.TestAuthUtils;
import io.extact.msa.spring.rms.remote.client.ItemResourceClient;
import io.extact.msa.spring.rms.remote.resources.ItemResource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "item", "test" })
@TestMethodOrder(OrderAnnotation.class)
public class RemoteItemResourceTest {

    private static final ItemResource item1 = new ItemResource(1, "A0001", "レンタル品1号");
    private static final ItemResource item2 = new ItemResource(2, "A0002", "レンタル品2号");
    private static final ItemResource item3 = new ItemResource(3, "A0003", "レンタル品3号");
    private static final ItemResource item4 = new ItemResource(4, "A0004", "レンタル品4号");


    @Autowired
    private ItemResourceClient client;

    @Configuration(proxyBeanMethods = false)
    @Import(RemoteResourcesApplication.class)
    static class TestConfig {

        @Bean
        @ConfigurationProperties("rms.persistence.item.remote")
        ExternalProperties externalProperties() {
            return new ExternalProperties();
        }

        @Bean
        ItemResourceClient itemResourceClient(ExternalProperties prop, ApplicationContext context) {
            return ClientFactoryUtils.createClient(prop, context, ItemResourceClient.class);
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
        Integer itemId = 1;
        // when
        ItemResource actual = client.get(itemId);
        // then
        assertThat(actual).isNotNull().isEqualTo(item1);

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
        List<ItemResource> expected = List.of(item1, item2, item3, item4);
        // when
        Collection<ItemResource> actual = client.getAll();
        // then
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testUpdate() {
        // given
        ItemResource updateItem = new ItemResource(4, "UPDATE", "UPDATE");
        // when
        boolean result = client.update(updateItem);
        // then
        assertThat(result).isTrue();
        assertThat(client.get(updateItem.id())).isEqualTo(updateItem);
    }

    @Test
    void testUpdateOnNotFound() {
        // given
        ItemResource notFoundItem = new ItemResource(999, "UPDATE", "UPDATE");
        // when
        boolean result = client.update(notFoundItem);
        // then
        assertThat(result).isFalse();
        assertThat(client.get(notFoundItem.id())).isNull();
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testAdd() {
        // given
        ItemResource addItem = new ItemResource(5, "ADD", "ADD");
        // when
        client.add(addItem);
        // then
        assertThat(client.get(addItem.id())).isNotNull().isEqualTo(addItem);
    }

    @Test
    @Order(WITH_SIDE_EFFECT)
    void testDelete() {
        // given
        int  deleteId = 3;
        // when
        boolean result = client.delete(deleteId);
        // then
        assertThat(result).isTrue();
        assertThat(client.get(deleteId)).isNull();
    }

    @Test
    void testDeleteOnNotFound() {
        // given
        int  notFoundId = 999;
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
        ItemResource foundItem = item1;
        // when
        ItemResource result = client.findBySerialNo(foundItem.serialNo());
        // then
        assertThat(result).isEqualTo(item1);

        // given
        String notFoundSerialNo = "xyz";
        // when
        result = client.findBySerialNo(notFoundSerialNo);
        // then
        assertThat(result).isNull();
    }
}
