package io.extact.msa.spring.rms.remote.resources.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.extact.msa.spring.platform.fw.interfaces.webapi.ApiController;
import io.extact.msa.spring.rms.remote.resources.ItemResource;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.ItemInMemoryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApiController("/items")
public class ItemResourceController extends RemoteResourceController<ItemResource> {

    private final ItemInMemoryRepository repository;

    @GetMapping("/unique")
    public ItemResource findBySerialNo(@RequestParam("serial-no") String serialNo) {
        return repository.findBySerialNo(serialNo);
    }

    @Override
    protected InMemoryRepository<ItemResource> repository() {
        return repository;
    }
}
