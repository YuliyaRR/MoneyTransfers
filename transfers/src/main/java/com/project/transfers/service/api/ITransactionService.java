package com.project.transfers.service.api;

import com.project.transfers.core.dto.Transaction;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {
    void saveTransaction(@NotNull Transaction transaction);
    List<Transaction> getAllAccountTransactions(@NotNull UUID acc);
}
