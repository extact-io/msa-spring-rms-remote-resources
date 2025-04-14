package io.extact.msa.spring.rms.remote.resources.repository;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;

import io.extact.msa.spring.rms.remote.resources.ItemResource;

public class ItemInMemoryRepository extends InMemoryRepository<ItemResource> {

    @Value("${rms.resources.item}")
    private File jsonFile;

    @Override
    protected File jsonFile() {
        return this.jsonFile;
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
