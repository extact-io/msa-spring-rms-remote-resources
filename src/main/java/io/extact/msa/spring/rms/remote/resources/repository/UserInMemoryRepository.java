package io.extact.msa.spring.rms.remote.resources.repository;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;

import io.extact.msa.spring.rms.remote.resources.UserResource;

public class UserInMemoryRepository extends InMemoryRepository<UserResource> {

    @Value("${rms.resources.user}")
    private File jsonFile;

    @Override
    protected File jsonFile() {
        return this.jsonFile;
    }

    @Override
    protected Class<UserResource> resourceClass() {
        return UserResource.class;
    }

    public UserResource findByLoginId(String loginId) {
        return resourceMap().values().stream()
                .filter(user -> user.loginId().equals(loginId))
                .findAny()
                .orElse(null);
    }

    public UserResource findByLoginIdAndPassword(String loginId, String password) {
        return resourceMap().values().stream()
                .filter(user -> user.loginId().equals(loginId))
                .filter(user -> user.password().equals(password))
                .findAny()
                .orElse(null);
    }
}
