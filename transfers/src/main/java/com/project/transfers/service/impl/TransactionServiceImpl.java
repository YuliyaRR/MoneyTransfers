package com.project.transfers.service.impl;


import com.project.transfers.core.dto.Transaction;

import com.project.transfers.core.dto.TransactionType;
import com.project.transfers.repository.api.TransactionRepo;
import com.project.transfers.repository.entity.AccountEntity;
import com.project.transfers.repository.entity.AccountSumOperationEntity;
import com.project.transfers.repository.entity.TransactionEntity;
import com.project.transfers.service.api.IAccountService;
import com.project.transfers.service.api.ITransactionService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Validated
@Service
public class TransactionServiceImpl implements ITransactionService {
    private final TransactionRepo repository;
    private final IAccountService accountService;
    private final ConversionService conversionService;

    @Override
    public void saveTransaction(@NotNull Transaction transaction) {
        List<AccountSumOperationEntity> operationList = createOperationList(transaction);

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .setId(transaction.getId())
                .setCurrency(transaction.getCurrency())
                .setType(transaction.getType())
                .setDate(transaction.getDate())
                .setAccountSumOperation(operationList)
                .build();

        repository.save(transactionEntity);

    }

    @Override
    public List<Transaction> getAllAccountTransactions(@NotNull UUID acc) {
        accountService.getByUUID(acc);//проверка существования
        List<TransactionEntity> transactionEntityList = repository.findAllByAccountSumOperation_AccountEntity_Num(acc);

        return transactionEntityList.stream()
                .map((entity) -> {
                    BigDecimal sum = entity.getAccountSumOperation().stream().filter(el -> el.getAccountEntity().getNum().equals(acc)).findFirst().get().getSum();
                    Transaction transaction = conversionService.convert(entity, Transaction.class);
                    transaction.setSum(sum.doubleValue());
                    return transaction;
                }).toList();
    }

    private AccountEntity getAccountEntity(UUID acc) {
        return Objects.isNull(acc) ? null : accountService.getByUUID(acc);
    }

    private List<AccountSumOperationEntity> createOperationList (Transaction transaction) {
        TransactionType type = transaction.getType();
        BigDecimal sum = BigDecimal.valueOf(transaction.getSum());

        List<AccountSumOperationEntity> listOperation = new ArrayList<>();

        switch (type) {
            case REPLENISH -> { //пополнение наличными
                UUID accountTo = transaction.getAccountTo();
                AccountEntity accountEntityTo = getAccountEntity(accountTo);

                listOperation.add(new AccountSumOperationEntity(sum, accountEntityTo));
            }
            case WITHDRAW -> { //снятие наличных
                UUID accountFrom = transaction.getAccountFrom();
                AccountEntity accountEntityFrom = getAccountEntity(accountFrom);

                listOperation.add(new AccountSumOperationEntity(sum.negate(), accountEntityFrom));
            }
            case TRANSFER -> { //переводы
                UUID accountTo = transaction.getAccountTo();
                AccountEntity accountEntityTo = getAccountEntity(accountTo);
                listOperation.add(new AccountSumOperationEntity(sum, accountEntityTo));

                UUID accountFrom = transaction.getAccountFrom();
                AccountEntity accountEntityFrom = getAccountEntity(accountFrom);
                listOperation.add(new AccountSumOperationEntity(sum.negate(), accountEntityFrom));
            }
        }

        return listOperation;
    }
}
