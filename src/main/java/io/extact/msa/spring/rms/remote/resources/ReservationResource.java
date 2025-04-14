package io.extact.msa.spring.rms.remote.resources;

import java.time.LocalDateTime;

public record ReservationResource(
        Integer id,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String note,
        Integer itemId,
        Integer reserverId) implements RemoteResource {
}
