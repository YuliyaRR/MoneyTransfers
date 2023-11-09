package com.project.transfers.web.controller;

import com.project.transfers.core.dto.User;
import com.project.transfers.service.api.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Tag(name = "Пользователи", description = "CRUD-операции с пользователями")
public class UserController {
    private final IUserService userService;

    @PostMapping(path = "/new")
    @Operation(summary = "Регистрация пользователя в системе", description = "Позволяет зарегистрировать пользователя в системе денежных переводов")
    public ResponseEntity<?> createUser(@RequestBody @Parameter(description = "Данные пользователя") @Valid User user) {
        userService.create(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{uuid}")
    @Operation(summary = "Получить пользователя", description = "Позволяет получить информацию о конкретном пользователе системы")
    public ResponseEntity<User> getUser(@PathVariable(name = "uuid")
                                            @Parameter(description = "Идентификатор пользователя") UUID uuid) {
        return new ResponseEntity<>(userService.getInfo(uuid), HttpStatus.OK);
    }

    @PutMapping(path = "/{uuid}/dt_update/{dt_update}")
    @Operation(summary = "Обновление данных пользователя", description = "Позволяет обновить информацию о конкретном пользователе системы")
    public ResponseEntity<?> updateUser(@PathVariable(name = "uuid")
                                            @Parameter(description = "Идентификатор пользователя") UUID uuid,
                                        @PathVariable(name = "dt_update")
                                           @Parameter(description = "Дата последнего обновления",
                                                   schema = @Schema(type = "string", format = "int64", example = "1630560292000")) @Past LocalDateTime dtUpdate,
                                           @RequestBody @Parameter(description = "Данные пользователя") @Valid User user) {
        userService.update(uuid, user, dtUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/{uuid}")
    @Operation(summary = "Удаление пользователя", description = "Позволяет удалить зарегистрованного пользователя из системы")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "uuid")
                                            @Parameter(description = "Идентификатор пользователя") UUID uuid) {
        userService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
