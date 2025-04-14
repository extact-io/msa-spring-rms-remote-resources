package io.extact.msa.spring.rms.remote.resources.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.extact.msa.spring.platform.fw.interfaces.webapi.RmsRestController;
import io.extact.msa.spring.rms.remote.resources.UserResource;
import io.extact.msa.spring.rms.remote.resources.repository.InMemoryRepository;
import io.extact.msa.spring.rms.remote.resources.repository.UserInMemoryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RmsRestController("/users")
public class UserResourceController extends RemoteResourceController<UserResource> {

    private final UserInMemoryRepository repository;

    @GetMapping("/unique")
    public UserResource findByLoginId(@RequestParam("login-id") String loginId) {
        return repository.findByLoginId(loginId);
    }

    @GetMapping("/auth")
    public UserResource findByLoginIdAndPassword(
            @RequestParam("login-id") String loginId,
            @RequestParam String password) {
        return repository.findByLoginIdAndPassword(loginId, password);
    }

    @Override
    protected InMemoryRepository<UserResource> repository() {
        return repository;
    }
}
