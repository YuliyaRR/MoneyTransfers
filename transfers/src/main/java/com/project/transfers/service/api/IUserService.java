package com.project.transfers.service.api;

import com.project.transfers.core.dto.User;
import com.project.transfers.repository.entity.UserEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IUserService {
    void create(@NotNull @Valid User user);
    User getInfo(@NotNull UUID uuid);
    void update(@NotNull UUID uuid, @NotNull @Valid User user, @NotNull @Past LocalDateTime dtUpd);
    void delete(@NotNull UUID uuid);
    UserEntity getByUUID (@NotNull UUID uuid);
    boolean doesUserExistByUUID(@NotNull UUID uuid);
}
