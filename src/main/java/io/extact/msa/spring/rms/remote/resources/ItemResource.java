package io.extact.msa.spring.rms.remote.resources;

public record ItemResource(
        Integer id,
        String serialNo,
        String itemName) implements RemoteResource {
}
