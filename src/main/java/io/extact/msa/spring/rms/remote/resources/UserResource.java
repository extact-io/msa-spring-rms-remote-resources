package io.extact.msa.spring.rms.remote.resources;

public record UserResource(
        Integer id,
        String loginId,
        String password,
        String userName,
        String phoneNumber,
        String contact,
        String userType) implements RemoteResource {
}
