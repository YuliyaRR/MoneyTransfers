package com.project.transfers.web.controller;

import com.project.transfers.core.dto.Account;
import com.project.transfers.core.dto.Payment;
import com.project.transfers.service.api.IAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/accounts")
public class AccountController {
    private final IAccountService accountService;

    @GetMapping(path = "/{uuid}")
    public ResponseEntity<Account> getAccount (@PathVariable(name = "uuid") UUID uuid) {
        return new ResponseEntity<>(accountService.getInfo(uuid), HttpStatus.OK);
    }

    @PostMapping(path = "/new")
    public ResponseEntity<?> createUser(@RequestBody @Valid Account account) {
        return new ResponseEntity<>( accountService.createAccount(account), HttpStatus.CREATED);
    }

    @PutMapping(path = "/replenish/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<?> replenishMoney(@PathVariable(name = "uuid") UUID uuid,
                                            @PathVariable(name = "dt_update") @Past LocalDateTime dtUpdate,
                                            @RequestBody @Valid Payment payment) {
        payment.setAccountTo(uuid);
        return new ResponseEntity<>(accountService.replenishAccount(payment, dtUpdate), HttpStatus.OK);
    }

    @PutMapping(path = "/withdraw/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<?> withdrawMoney(@PathVariable(name = "uuid") UUID uuid,
                                            @PathVariable(name = "dt_update") @Past LocalDateTime dtUpdate,
                                            @RequestBody @Valid Payment payment) {
        payment.setAccountFrom(uuid);
        return new ResponseEntity<>(accountService.withdrawalMoney(payment, dtUpdate), HttpStatus.OK);
    }

    @PutMapping(path = "/transfer/{uuid}/dt_update/{dt_update}")
    public ResponseEntity<?> transferMoney(@PathVariable(name = "uuid") @NotNull UUID uuid,
                                           @PathVariable(name = "dt_update") @Past LocalDateTime dtUpdate,
                                           @RequestBody @Valid Payment payment) {
        payment.setAccountFrom(uuid);
        return new ResponseEntity<>(accountService.transferMoney(payment, dtUpdate), HttpStatus.OK);
    }


}
