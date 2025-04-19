package io.extact.msa.spring.rms.remote.resources.repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.rms.remote.resources.RemoteResource;

public abstract class InMemoryRepository<R extends RemoteResource> {

    @Value("${rms.resources.format:yyyyMMdd HH:mm}")
    private String format;

    private Map<Integer, R> resourceMap;

    @PostConstruct
    protected void init() throws IOException {

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(format)));

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(module)
                .build();

        TypeFactory typeFactory = mapper.getTypeFactory();
        JavaType listType = typeFactory.constructCollectionType(List.class, resourceClass());
        List<R> reservations = mapper.readValue(jsonFile(), listType);

        resourceMap = reservations.stream()
                .collect(Collectors.toMap(
                        R::id,
                        reservation -> reservation,
                        (existing, _) -> {
                            throw new IllegalStateException(String.format("Duplicate key found: %s", existing.id()));
                        },
                        LinkedHashMap::new));
    }

    public R get(@PathVariable Integer id) {
        return getAll().stream()
                .filter(entity -> entity.id().equals(id))
                .findAny()
                .orElse(null);
    }

    public Collection<R> getAll() {
        return resourceMap().values();
    }

    public void add(@RequestBody R resource) {
        if (resourceMap().putIfAbsent(resource.id(), resource) != null) {
            throw new BusinessFlowException("Already exists. key:" + resource.id(), CauseType.DUPLICATE);
        }
    }

    public boolean update(@RequestBody R resource) {
        return resourceMap().computeIfPresent(resource.id(), (_, _) -> resource) != null;
    }

    public boolean delete(@PathVariable Integer id) {
        return resourceMap().remove(id) != null;
    }

    public int nextIdentity() {
        return Collections.max(resourceMap().keySet()) + 1;
    }

    protected Map<Integer, R> resourceMap() {
        return resourceMap;
    }

    protected abstract Class<R> resourceClass();

    protected abstract File jsonFile();
}
