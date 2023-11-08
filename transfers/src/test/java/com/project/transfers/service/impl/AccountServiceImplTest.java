package com.project.transfers.service.impl;

import com.project.transfers.core.dto.*;
import com.project.transfers.core.error.ErrorCode;
import com.project.transfers.core.event.TransactionEvent;
import com.project.transfers.core.exceptions.ConversionTimeException;
import com.project.transfers.core.exceptions.NotFoundDataBaseException;
import com.project.transfers.core.exceptions.PaymentException;
import com.project.transfers.repository.api.AccountRepo;
import com.project.transfers.repository.entity.AccountEntity;
import com.project.transfers.repository.entity.UserEntity;
import com.project.transfers.service.api.IAccountService;
import com.project.transfers.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepo repository;
    @Mock
    private IUserService userService;
    @Mock
    private ConversionService conversionService;
    @Mock
    private ApplicationEventPublisher publisher;
    private IAccountService accountService;
    private AccountEntity accountEntity;
    private Transaction transaction;
    private Payment payment;
    private Account account;
    private UUID userUUID = UUID.randomUUID();
    private UUID accountFromUUID = UUID.randomUUID();
    private UUID accountToUUID = UUID.randomUUID();
    private final static String EXCEPTION_ACCOUNT_NOT_FOUND = "Account wasn't found in the data base";
    private final static String EXCEPTION_ACCOUNT_NOT_FOUND_VERSION = "Account with this version wasn't found in the database";
    private final static String EXCEPTION_UNABLE_TO_CONVERT = "Unable to convert";
    private final static String EXCEPTION_NOT_SPECIFY_ACCOUNT = "You didn't specify the beneficiary's account";
    private final static String EXCEPTION_INVALID_CURRENCY = "Account currency does not match the payment currency";
    private final static String EXCEPTION_INVALID_BALANCE = "You don't have enough money for this operation";

    @BeforeEach
    public void setUp() {
        this.accountService = new AccountServiceImpl(repository, userService, conversionService, publisher);
        this.accountFromUUID = UUID.randomUUID();
        this.accountToUUID = UUID.randomUUID();
        this.userUUID = UUID.randomUUID();

        this.account = Account.builder()
                .setCurrency(Currency.USD)
                .setBalance(BigDecimal.valueOf(100.20))
                .setOwner(new User(userUUID))
                .build();

        LocalDateTime dateTime = LocalDateTime.of(2020,10, 13, 14, 15,13);
        this.accountEntity = AccountEntity.builder()
                .setCurrency(Currency.USD)
                .setBalance(BigDecimal.valueOf(100.20))
                .setDateOpen(dateTime)
                .setDateLastUpd(dateTime)
                .build();

        this.transaction = Transaction.builder()
                .setId(UUID.randomUUID())
                .setCurrency(Currency.USD)
                .setSum(100.20)
                .setDate(LocalDateTime.now())
                .build();

        this.payment = new Payment(null, null, Currency.USD, 100.20, null);
    }

    @Test
    public void createAccountWhenDataIsValidThenReturnTransaction() {
        accountEntity.setNum(accountToUUID);

        when(userService.getByUUID(userUUID)).thenReturn(new UserEntity());
        when(conversionService.canConvert(Account.class, AccountEntity.class)).thenReturn(true);
        when(conversionService.convert(account, AccountEntity.class)).thenReturn(accountEntity);

        Transaction actualTransaction = accountService.createAccount(account);

        assertNotNull(actualTransaction);
        assertEquals(transaction.getCurrency(), actualTransaction.getCurrency());
        assertEquals(transaction.getSum(), actualTransaction.getSum());

        verify(repository, times(1)).save(accountEntity);
        verify(publisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void createAccountWhenOwnerNotFoundThenThrowException() {
        when(userService.getByUUID(userUUID)).thenThrow(NotFoundDataBaseException.class);

        assertThrows(NotFoundDataBaseException.class, () -> accountService.createAccount(account));

        verify(conversionService, never()).canConvert(Account.class, AccountEntity.class);
        verify(conversionService, never()).convert(account, AccountEntity.class);
        verify(repository, never()).save(any(AccountEntity.class));
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void createAccountWhenConverterNotFoundThenThrowException() {
        when(userService.getByUUID(userUUID)).thenReturn(new UserEntity());
        when(conversionService.canConvert(Account.class, AccountEntity.class)).thenReturn(false);

        ConversionTimeException exception = assertThrows(ConversionTimeException.class, () -> accountService.createAccount(account));
        assertEquals(EXCEPTION_UNABLE_TO_CONVERT, exception.getMessage());

        verify(conversionService, never()).convert(account, AccountEntity.class);
        verify(repository, never()).save(any(AccountEntity.class));
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }


    @Test
    public void replenishAccountWhenDataIsValidThenReturnTransactions() {
        payment.setAccountTo(accountToUUID);
        accountEntity.setNum(accountToUUID);

        when(repository.findById(accountToUUID)).thenReturn(Optional.of(accountEntity));

        Transaction actualTransaction = accountService.replenishAccount(payment, accountEntity.getDateLastUpd());

        assertNotNull(actualTransaction);
        assertEquals(payment.getCurrency(), actualTransaction.getCurrency());
        assertEquals(payment.getSum(), actualTransaction.getSum());
        assertEquals(payment.getAccountTo(), actualTransaction.getAccountTo());
        assertEquals(TransactionType.REPLENISH, actualTransaction.getType());

        verify(repository, times(1)).save(accountEntity);
        verify(publisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void replenishAccountWhenAccountNotFoundThenThrowException() {
        payment.setAccountTo(accountToUUID);

        when(repository.findById(accountToUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.replenishAccount(payment, accountEntity.getDateLastUpd()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, times(0)).save(accountEntity);
        verify(publisher, times(0)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void replenishAccountWhenAccountVersionNotValidThenThrowException() {
        payment.setAccountTo(accountToUUID);
        accountEntity.setNum(accountToUUID);

        when(repository.findById(accountToUUID)).thenReturn(Optional.of(accountEntity));

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.replenishAccount(payment, LocalDateTime.now()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND_VERSION, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, times(0)).save(accountEntity);
        verify(publisher, times(0)).publishEvent(any(TransactionEvent.class));
    }


    @Test
    public void withdrawalMoneyWhenDataIsValidThenReturnTransactions() {
        payment.setAccountFrom(accountFromUUID);
        accountEntity.setNum(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountEntity));

        Transaction actualTransaction = accountService.withdrawalMoney(payment, accountEntity.getDateLastUpd());

        assertNotNull(actualTransaction);
        assertEquals(payment.getCurrency(), actualTransaction.getCurrency());
        assertEquals(payment.getSum(), actualTransaction.getSum());
        assertEquals(payment.getAccountFrom(), actualTransaction.getAccountFrom());
        assertEquals(TransactionType.WITHDRAW, actualTransaction.getType());

        verify(repository, times(1)).save(accountEntity);
        verify(publisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void withdrawalMoneyWhenAccountNotFoundThenThrowException() {
        payment.setAccountFrom(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.withdrawalMoney(payment, accountEntity.getDateLastUpd()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).save(accountEntity);
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void withdrawalMoneyWhenAccountVersionNotValidThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        accountEntity.setNum(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountEntity));

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.withdrawalMoney(payment, LocalDateTime.now()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND_VERSION, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, times(0)).save(accountEntity);
        verify(publisher, times(0)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAllDataIsValidThenReturnTransaction() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);

        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);

        AccountEntity accountTo = new AccountEntity();
        accountTo.setNum(accountToUUID);
        accountTo.setCurrency(Currency.USD);
        accountTo.setBalance(BigDecimal.valueOf(100));


        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));
        when(repository.findById(accountToUUID)).thenReturn(Optional.of(accountTo));

        Transaction actualTransaction = accountService.transferMoney(payment, accountFrom.getDateLastUpd());

        assertNotNull(actualTransaction);
        assertEquals(payment.getCurrency(), actualTransaction.getCurrency());
        assertEquals(payment.getSum(), actualTransaction.getSum());
        assertEquals(payment.getAccountFrom(), actualTransaction.getAccountFrom());
        assertEquals(payment.getAccountTo(), actualTransaction.getAccountTo());
        assertEquals(TransactionType.TRANSFER, actualTransaction.getType());

        verify(repository, times(1)).saveAll(any());
        verify(publisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountToInPaymentIsNullThenThrowException() {
        PaymentException exception = assertThrows(PaymentException.class, () -> accountService.transferMoney(payment, accountEntity.getDateLastUpd()));
        assertEquals(EXCEPTION_NOT_SPECIFY_ACCOUNT, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountFromNotFoundThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.transferMoney(payment, accountEntity.getDateLastUpd()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountFromVersionNotValidThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);
        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.transferMoney(payment, LocalDateTime.now()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND_VERSION, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountFromCurrencyNotValidThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setCurrency(Currency.RUB);
        payment.setAccountTo(accountToUUID);
        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));

        PaymentException exception = assertThrows(PaymentException.class, () -> accountService.transferMoney(payment, accountFrom.getDateLastUpd()));
        assertEquals(EXCEPTION_INVALID_CURRENCY, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountFromBalanceNotValidThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);
        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);
        accountFrom.setBalance(BigDecimal.ONE);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));

        PaymentException exception = assertThrows(PaymentException.class, () -> accountService.transferMoney(payment, accountFrom.getDateLastUpd()));
        assertEquals(EXCEPTION_INVALID_BALANCE, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountToNotFoundThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);
        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));
        when(repository.findById(accountToUUID)).thenReturn(Optional.empty());


        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.transferMoney(payment, accountFrom.getDateLastUpd()));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }

    @Test
    public void transferMoneyWhenAccountToCurrencyNotValidThenThrowException() {
        payment.setAccountFrom(accountFromUUID);
        payment.setAccountTo(accountToUUID);
        AccountEntity accountFrom = accountEntity;
        accountFrom.setNum(accountFromUUID);
        AccountEntity accountTo = new AccountEntity();
        accountTo.setNum(accountToUUID);
        accountTo.setCurrency(Currency.RUB);

        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountFrom));
        when(repository.findById(accountToUUID)).thenReturn(Optional.of(accountTo));

        PaymentException exception = assertThrows(PaymentException.class, () -> accountService.transferMoney(payment, accountFrom.getDateLastUpd()));
        assertEquals(EXCEPTION_INVALID_CURRENCY, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(repository, never()).saveAll(any());
        verify(publisher, never()).publishEvent(any(TransactionEvent.class));
    }
    @Test
    public void getInfoWhenConverterWasFoundThenReturnAccount() {
        accountEntity.setNum(accountFromUUID);

        when(conversionService.canConvert(AccountEntity.class, Account.class)).thenReturn(true);
        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountEntity));
        when(conversionService.convert(accountEntity, Account.class)).thenReturn(account);

        Account info = accountService.getInfo(accountFromUUID);

        assertNotNull(info);
    }

    @Test
    public void getInfoWhenConverterNotFoundThenThrowException() {
        when(conversionService.canConvert(AccountEntity.class, Account.class)).thenReturn(false);

        ConversionTimeException exception = assertThrows(ConversionTimeException.class, () -> accountService.getInfo(accountFromUUID));
        assertEquals(EXCEPTION_UNABLE_TO_CONVERT, exception.getMessage());

        verify(conversionService, never()).convert(accountEntity, Account.class);
        verify(repository, never()).findById(accountFromUUID);
    }

    @Test
    public void getInfoWhenAccountNotFoundThenThrowException() {
        when(conversionService.canConvert(AccountEntity.class, Account.class)).thenReturn(true);
        when(repository.findById(accountFromUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.getInfo(accountFromUUID));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());

        verify(conversionService, never()).convert(accountEntity, Account.class);
        verify(repository, times(1)).findById(accountFromUUID);
    }

    @Test
    public void getByUUIDWhenAccountWasFoundThenReturnEntity() {
        when(repository.findById(accountFromUUID)).thenReturn(Optional.of(accountEntity));

        AccountEntity entity = accountService.getByUUID(accountFromUUID);

        assertNotNull(entity);

        verify(repository, times(1)).findById(accountFromUUID);
    }

    @Test
    public void getByUUIDWhenAccountNotFoundThenThrowException() {
        when(repository.findById(accountFromUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> accountService.getByUUID(accountFromUUID));
        assertEquals(EXCEPTION_ACCOUNT_NOT_FOUND, exception.getMessage());

        verify(repository, times(1)).findById(accountFromUUID);
    }
}