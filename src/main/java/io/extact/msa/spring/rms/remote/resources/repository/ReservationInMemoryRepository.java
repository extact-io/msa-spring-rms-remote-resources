package io.extact.msa.spring.rms.remote.resources.repository;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import io.extact.msa.spring.rms.remote.resources.ReservationResource;

public class ReservationInMemoryRepository extends InMemoryRepository<ReservationResource> {

    @Value("${rms.resources.reservation}")
    private File jsonFile;

    @Override
    protected File jsonFile() {
        return this.jsonFile;
    }

    @Override
    protected Class<ReservationResource> resourceClass() {
        return ReservationResource.class;
    }

    public List<ReservationResource> findByCondition(Integer itemId, Integer reserverId, LocalDate from) {
        return resourceMap().values().stream()
                .filter(r -> itemId == null || itemId.equals(r.itemId()))
                .filter(r -> reserverId == null || reserverId.equals(r.reserverId()))
                .filter(r -> from == null || from.equals(r.fromDateTime().toLocalDate()))
                .toList();
    }
}
