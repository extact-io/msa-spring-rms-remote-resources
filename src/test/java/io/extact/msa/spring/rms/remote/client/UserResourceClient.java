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

import io.extact.msa.spring.rms.remote.resources.UserResource;

@HttpExchange("/remote/users")
public interface UserResourceClient {

    @GetExchange("/{id}")
    UserResource get(@PathVariable Integer id);

    @GetExchange
    Collection<UserResource> getAll();

    @PostExchange
    void add(@RequestBody UserResource resource);

    @PutExchange
    boolean update(@RequestBody UserResource resource);

    @DeleteExchange("/{id}")
    boolean delete(@PathVariable Integer id);

    @GetExchange("/next-identity")
    int nextIdentity();

    @GetExchange("/unique")
    UserResource findByLoginId(@RequestParam("login-id") String loginId);

    @GetExchange("/auth")
    UserResource findByLoginIdAndPassword(
            @RequestParam("login-id") String loginId,
            @RequestParam String password);
}
