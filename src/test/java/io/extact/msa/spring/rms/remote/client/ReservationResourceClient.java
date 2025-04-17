package io.extact.msa.spring.rms.remote.client;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import io.extact.msa.spring.rms.remote.resources.ReservationResource;

@HttpExchange("/remote/reservations")
public interface ReservationResourceClient {

    @GetExchange("/{id}")
    ReservationResource get(@PathVariable Integer id);

    @GetExchange
    Collection<ReservationResource> getAll();

    @PostExchange
    void add(@RequestBody ReservationResource resource);

    @PutExchange
    boolean update(@RequestBody ReservationResource resource);

    @DeleteExchange("/{id}")
    boolean delete(@PathVariable Integer id);

    @GetExchange("/next-identity")
    int nextIdentity();

    @GetExchange("/cond")
    List<ReservationResource> findByCondition(
            @RequestParam(name = "item-id", required = false) Integer itemId,
            @RequestParam(name = "reserver-id", required = false) Integer reserverId,
            @RequestParam(name = "from-date", required = false) LocalDate from);
}
