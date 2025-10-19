package io.extact.msa.spring.rms.remote.resources.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.extact.msa.spring.platform.fw.interfaces.webapi.ApiController;
import io.extact.msa.spring.rms.remote.resources.ReservationResource;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.ReservationInMemoryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApiController("/reservations")
public class ReservationResourceController extends RemoteResourceController<ReservationResource> {

    private final ReservationInMemoryRepository repository;

    @GetMapping("/cond")
    public List<ReservationResource> findByCondition(
            @RequestParam(name = "item-id", required = false) Integer itemId,
            @RequestParam(name = "reserver-id", required = false) Integer reserverId,
            @RequestParam(name = "from-date", required = false) LocalDate from) {

        return repository.findByCondition(itemId, reserverId, from);
    }

    @Override
    protected InMemoryRepository<ReservationResource> repository() {
        return repository;
    }
}
