package com.project.transfers.service.api;

import com.project.transfers.core.dto.Account;
import com.project.transfers.core.dto.Payment;
import com.project.transfers.core.dto.Transaction;
import com.project.transfers.repository.entity.AccountEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IAccountService {
    Transaction createAccount(@NotNull @Valid Account account);
    Transaction replenishAccount(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd);
    Transaction withdrawalMoney(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd);
    Transaction transferMoney(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd);
    Account getInfo(@NotNull UUID account);
    AccountEntity getByUUID(@NotNull UUID account);
}
