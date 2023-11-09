package com.project.transfers.service.impl;

import com.project.transfers.core.dto.Currency;
import com.project.transfers.core.dto.Transaction;
import com.project.transfers.core.dto.TransactionType;
import com.project.transfers.repository.api.TransactionRepo;
import com.project.transfers.repository.entity.AccountEntity;
import com.project.transfers.repository.entity.AccountSumOperationEntity;
import com.project.transfers.repository.entity.TransactionEntity;
import com.project.transfers.service.api.IAccountService;
import com.project.transfers.service.api.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    @Mock
    private TransactionRepo repository;
    @Mock
    private IAccountService accountService;
    @Mock
    private ConversionService conversionService;
    private ITransactionService transactionService;
    private Transaction transaction;
    private AccountEntity accountEntity;
    private UUID accUUID = UUID.randomUUID();
    @Captor
    private ArgumentCaptor<TransactionEntity> captorTransaction;


    @BeforeEach
    public void setUp() {
        this.transactionService = new TransactionServiceImpl(repository, accountService, conversionService);
        this.transaction = Transaction.builder()
                .setId(UUID.randomUUID())
                .setCurrency(Currency.USD)
                .setSum(100.20)
                .setDate(LocalDateTime.now())
                .build();
        LocalDateTime dateTime = LocalDateTime.of(2020,10, 13, 14, 15,13);
        this.accountEntity = AccountEntity.builder()
                .setCurrency(Currency.USD)
                .setBalance(BigDecimal.valueOf(100.20))
                .setDateOpen(dateTime)
                .setDateLastUpd(dateTime)
                .build();
    }

    @Test
    public void saveTransactionWhenTypeReplenishThenSumOperationIsEqualsSumInTransaction() {
        transaction.setType(TransactionType.REPLENISH);
        transaction.setAccountTo(accUUID);
        accountEntity.setNum(accUUID);

        when(accountService.getByUUID(accUUID)).thenReturn(accountEntity);

        transactionService.saveTransaction(transaction);

        verify(repository).save(captorTransaction.capture());

        TransactionEntity transactionEntity = captorTransaction.getValue();
        List<AccountSumOperationEntity> list = transactionEntity.getAccountSumOperation();

        assertEquals(transaction.getType(), transactionEntity.getType());
        assertEquals(1, list.size());
        assertEquals(transaction.getSum(), list.get(0).getSum().doubleValue());

        verify(repository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    public void saveTransactionWhenTypeWithdrawThenSumOperationIsNegative() {
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAccountFrom(accUUID);
        accountEntity.setNum(accUUID);

        when(accountService.getByUUID(accUUID)).thenReturn(accountEntity);

        transactionService.saveTransaction(transaction);

        verify(repository).save(captorTransaction.capture());

        TransactionEntity transactionEntity = captorTransaction.getValue();
        List<AccountSumOperationEntity> list = transactionEntity.getAccountSumOperation();

        assertEquals(transaction.getType(), transactionEntity.getType());
        assertEquals(1, list.size());
        assertEquals(-transaction.getSum(), list.get(0).getSum().doubleValue());

        verify(repository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    public void saveTransactionWhenTypeTransferThenSumOperationHasTwoValue() {
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAccountFrom(accUUID);
        UUID addUUID = UUID.randomUUID();
        transaction.setAccountTo(addUUID);
        accountEntity.setNum(accUUID);
        AccountEntity addAccountEntity = AccountEntity.builder()
                .setNum(addUUID)
                .build();

        when(accountService.getByUUID(accUUID)).thenReturn(accountEntity);
        when(accountService.getByUUID(addUUID)).thenReturn(addAccountEntity);

        transactionService.saveTransaction(transaction);

        verify(repository).save(captorTransaction.capture());

        TransactionEntity transactionEntity = captorTransaction.getValue();

        List<AccountSumOperationEntity> list = transactionEntity.getAccountSumOperation();
        AccountSumOperationEntity accountSumOperation0 = list.get(0);
        AccountSumOperationEntity accountSumOperation1 = list.get(1);

        assertEquals(transaction.getType(), transactionEntity.getType());
        assertEquals(2, list.size());
        assertEquals(transaction.getSum(), accountSumOperation0.getSum().doubleValue());
        assertEquals(-transaction.getSum(), accountSumOperation1.getSum().doubleValue());

        verify(repository, times(1)).save(any(TransactionEntity.class));
    }
}