package io.extact.msa.spring.rms.remote.client;

import java.util.Collection;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import io.extact.msa.spring.rms.remote.resources.ItemResource;

@HttpExchange("/remote/items")
public interface ItemResourceClient {

    @GetExchange("/{id}")
    ItemResource get(@PathVariable Integer id);

    @GetExchange
    Collection<ItemResource> getAll();

    @PostExchange
    void add(@RequestBody ItemResource resource);

    @PutExchange
    boolean update(@RequestBody ItemResource resource);

    @DeleteExchange("/{id}")
    boolean delete(@PathVariable Integer id);

    @GetExchange("/next-identity")
    int nextIdentity();

    @GetExchange("/unique")
    ItemResource findBySerialNo(@RequestParam("serial-no") String serialNo);
}
