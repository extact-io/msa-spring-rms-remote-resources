package io.extact.msa.spring.rms.remote.resources.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.extact.msa.spring.rms.remote.resources.RemoteResource;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepository;

public abstract class RemoteResourceController<R extends RemoteResource> {

    @GetMapping("/reset")
    public void reset() throws IOException {
        repository().init();
    }

    @GetMapping("/{id}")
    public R get(@PathVariable Integer id) {
        return repository().get(id);
    }

    @GetMapping
    public Collection<R> getAll() {
        return repository().getAll();
    }

    @PostMapping
    public void add(@RequestBody R resource) {
        repository().add(resource);
    }

    @PutMapping
    public boolean update(@RequestBody R resource) {
        return repository().update(resource);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return repository().delete(id);
    }

    @GetMapping("/next-identity")
    public int nextIdentity() {
        return repository().nextIdentity();
    }

    protected abstract InMemoryRepository<R> repository();

}
