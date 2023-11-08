package com.project.transfers.web.controller;

import com.project.transfers.core.dto.User;
import com.project.transfers.service.api.IUserService;
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
public class UserController {
    private final IUserService userService;

    @PostMapping(path = "/new")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        userService.create(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

        @GetMapping(path = "/{uuid}")
    public ResponseEntity<User> getUser(@PathVariable(name = "uuid") UUID uuid) {
        return new ResponseEntity<>(userService.getInfo(uuid), HttpStatus.OK);
    }

    @PutMapping(path = "/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "uuid") UUID uuid,
                                           @PathVariable(name = "dt_update") @Past LocalDateTime dtUpdate,
                                           @RequestBody @Valid User user) {
        userService.update(uuid, user, dtUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "uuid") UUID uuid) {
        userService.delete(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
