package com.project.transfers.web.controller;

import com.project.transfers.core.dto.Transaction;
import com.project.transfers.service.api.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/transactions")
@Tag(name = "Транзакции", description = "Транзакции по счетам пользователей")
public class TransactionalController {
    private final ITransactionService transactionService;

    @GetMapping(path = "/{uuid}")
    @Operation(summary = "Информация о транзакциях по счету", description = "Позволяет получить информацию о проведенных транзакциях по счету клиента")
    public ResponseEntity<List<Transaction>> getAllTransactionsByAccount(@PathVariable(name = "uuid")
                                                                             @Parameter(description = "Номер счета") UUID uuid) {
        return new ResponseEntity<>(transactionService.getAllAccountTransactions(uuid), HttpStatus.OK);
    }
}
