package com.project.transfers.web.controller;

import com.project.transfers.core.dto.Transaction;
import com.project.transfers.service.api.ITransactionService;
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
public class TransactionalController {
    private final ITransactionService transactionService;

    @GetMapping(path = "/{uuid}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByAccount(@PathVariable(name = "uuid") UUID uuid){
        return new ResponseEntity<>(transactionService.getAllAccountTransactions(uuid), HttpStatus.OK);
    }
}
