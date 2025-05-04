package io.extact.msa.spring.rms.remote.resources.repository;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import io.extact.msa.spring.rms.remote.resources.ItemResource;

public class ItemInMemoryRepository extends InMemoryRepository<ItemResource> {

    @Value("${rms.resources.item}")
    private Resource jsonFile;

    @Override
    protected InputStream jsonFile() throws IOException {
        return this.jsonFile.getInputStream();
    }

    @Override
    protected Class<ItemResource> resourceClass() {
        return ItemResource.class;
    }

    public ItemResource findBySerialNo(String serialNo) {
        return resourceMap().values().stream()
                .filter(item -> item.serialNo().equals(serialNo))
                .findAny()
                .orElse(null);
    }
}
