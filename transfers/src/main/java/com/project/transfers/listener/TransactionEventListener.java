package com.project.transfers.listener;

import com.project.transfers.core.dto.Transaction;
import com.project.transfers.core.event.TransactionEvent;
import com.project.transfers.service.api.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {
    private final ITransactionService transactionService;

    @EventListener
    public void handleTransaction(TransactionEvent event) {
        Transaction transaction = event.transaction();
        transactionService.saveTransaction(transaction);
    }
}
