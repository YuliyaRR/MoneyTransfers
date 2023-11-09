package com.project.transfers.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.transfers.core.dto.Account;
import com.project.transfers.core.dto.Payment;
import com.project.transfers.core.dto.Transaction;
import com.project.transfers.core.view.Views;
import com.project.transfers.service.api.IAccountService;
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
@RequestMapping(path = "/accounts")
@Tag(name = "Счета пользователей", description = "Операции со счетами пользователей")
public class AccountController {
    private final IAccountService accountService;

    @GetMapping(path = "/{uuid}")
    @Operation(summary = "Информация о счете клиента", description = "Позволяет получить информацию о счете пользователя")
    public ResponseEntity<Account> getAccount (@PathVariable(name = "uuid")
                                                   @Parameter(description = "Номер счета") UUID uuid) {
        return new ResponseEntity<>(accountService.getInfo(uuid), HttpStatus.OK);
    }

    @PostMapping(path = "/new")
    @Operation(summary = "Открытие счета", description = "Позволяет открыть новый счет зарегистрированному пользователю и зачислить первоначальный взнос на счет")
    @JsonView({Views.transactionalOutAccountTo.class})
    public ResponseEntity<Transaction> createUser(@RequestBody
                                                      @Parameter (description = "Информация для открытия счета")
                                                      @Valid Account account) {
        return new ResponseEntity<>( accountService.createAccount(account), HttpStatus.CREATED);
    }

    @PutMapping(path = "/replenish/{uuid}/dt_update/{dt_update}")
    @Operation(summary = "Пополнение счета", description = "Позволяет пользователю пополнить счет путем взноса наличными")
    @JsonView({Views.transactionalOutAccountTo.class})
    public ResponseEntity<Transaction> replenishMoney(@PathVariable(name = "uuid")
                                                @Parameter(description = "Номер счета") UUID uuid,
                                            @PathVariable(name = "dt_update")
                                                @Parameter(description = "Дата последнего обновления",
                                                    schema = @Schema(type = "string", format = "int64", example = "1630560292000")) @Past LocalDateTime dtUpdate,
                                            @RequestBody @Parameter(description = "Данные платежа") @Valid Payment payment) {
        payment.setAccountTo(uuid);
        return new ResponseEntity<>(accountService.replenishAccount(payment, dtUpdate), HttpStatus.OK);
    }

    @PutMapping(path = "/withdraw/{uuid}/dt_update/{dt_update}")
    @Operation(summary = "Снятие со счета", description = "Позволяет пользователю обналичить счет")
    @JsonView({Views.transactionalOutAccountFrom.class})
    public ResponseEntity<Transaction> withdrawMoney(@PathVariable(name = "uuid")
                                               @Parameter(description = "Номер счета") UUID uuid,
                                           @PathVariable(name = "dt_update")
                                               @Parameter(description = "Дата последнего обновления",
                                                       schema = @Schema(type = "string", format = "int64", example = "1630560292000")) @Past LocalDateTime dtUpdate,
                                           @RequestBody @Parameter(description = "Данные платежа") @Valid Payment payment) {
        payment.setAccountFrom(uuid);
        return new ResponseEntity<>(accountService.withdrawalMoney(payment, dtUpdate), HttpStatus.OK);
    }

    @PutMapping(path = "/transfer/{uuid}/dt_update/{dt_update}")
    @Operation(summary = "Денежные переводы", description = "Позволяет осуществлять денежные переводы между счетами внутри системы")
    @JsonView(Views.transactionalOutTransfer.class)
    public ResponseEntity<Transaction> transferMoney(@PathVariable(name = "uuid")
                                                         @Parameter(description = "Номер счета") UUID uuid,
                                                     @PathVariable(name = "dt_update")
                                                         @Parameter(description = "Дата последнего обновления",
                                                                 schema = @Schema(type = "string", format = "int64", example = "1630560292000")) @Past LocalDateTime dtUpdate,
                                                     @RequestBody @Parameter(description = "Данные платежа") @Valid Payment payment) {
        payment.setAccountFrom(uuid);
        return new ResponseEntity<>(accountService.transferMoney(payment, dtUpdate), HttpStatus.OK);
    }


}
