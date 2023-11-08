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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@Service
public class AccountServiceImpl implements IAccountService {
    private final AccountRepo repository;
    private final IUserService userService;
    private final ConversionService conversionService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public Transaction createAccount(@NotNull @Valid Account account) {
        UserEntity userEntity = userService.getByUUID(account.getOwner().getId());

        if (!conversionService.canConvert(Account.class, AccountEntity.class)) {
            throw new ConversionTimeException("Unable to convert", ErrorCode.ERROR);
        }

        AccountEntity accountEntity = conversionService.convert(account, AccountEntity.class);
        accountEntity.setOwner(userEntity);

        repository.save(accountEntity);

        Transaction transaction = Transaction.builder()
                .setId(UUID.randomUUID())
                .setCurrency(account.getCurrency())
                .setAccountTo(accountEntity.getNum())
                .setSum(account.getBalance().doubleValue())
                .setType(TransactionType.REPLENISH)
                .setDate(LocalDateTime.now())
                .build();

        publisher.publishEvent(new TransactionEvent(transaction));

        return transaction;
    }

    @Override
    @Transactional
    public Transaction replenishAccount(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd) {
        UUID accountTo = payment.getAccountTo();
        AccountEntity accountEntity = getByUUID(accountTo);

        if (accountEntity.getDateLastUpd().equals(dtUpd)) {

            checkCurrency(accountEntity.getCurrency(), payment.getCurrency());

            BigDecimal balance = accountEntity.getBalance();
            balance = balance.add(BigDecimal.valueOf(payment.getSum()));

            accountEntity.setBalance(balance);

            repository.save(accountEntity);

            Transaction transaction = Transaction.builder()
                    .setId(UUID.randomUUID())
                    .setCurrency(payment.getCurrency())
                    .setAccountTo(accountEntity.getNum())
                    .setSum(payment.getSum())
                    .setType(TransactionType.REPLENISH)
                    .setDate(LocalDateTime.now())
                    .build();

            publisher.publishEvent(new TransactionEvent(transaction));

            return transaction;
        } else {
            throw new NotFoundDataBaseException("Account with this version wasn't found in the database", ErrorCode.ERROR);
        }
    }

    @Override
    @Transactional
    public Transaction withdrawalMoney(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd) {
        UUID accountFrom = payment.getAccountFrom();
        AccountEntity accountEntity = getByUUID(accountFrom);

        if (accountEntity.getDateLastUpd().equals(dtUpd)) {

            checkCurrency(accountEntity.getCurrency(), payment.getCurrency());

            BigDecimal balance = accountEntity.getBalance();
            BigDecimal sumPayment = BigDecimal.valueOf(payment.getSum());

            checkBalance(balance, sumPayment);

            balance = balance.subtract(sumPayment);

            accountEntity.setBalance(balance);

            repository.save(accountEntity);

            Transaction transaction = Transaction.builder()
                    .setId(UUID.randomUUID())
                    .setCurrency(payment.getCurrency())
                    .setAccountFrom(accountEntity.getNum())
                    .setSum(payment.getSum())
                    .setType(TransactionType.WITHDRAW)
                    .setDate(LocalDateTime.now())
                    .build();

            publisher.publishEvent(new TransactionEvent(transaction));

            return transaction;
        } else {
            throw new NotFoundDataBaseException("Account with this version wasn't found in the database", ErrorCode.ERROR);
        }

    }

    @Override
    @Transactional
    public Transaction transferMoney(@NotNull @Valid Payment payment, @NotNull @Past LocalDateTime dtUpd) {
        UUID accountTo = payment.getAccountTo();
        UUID accountFrom = payment.getAccountFrom();
        BigDecimal sumPayment = BigDecimal.valueOf(payment.getSum());
        Currency paymentCurrency = payment.getCurrency();

        if(Objects.isNull(accountTo)) {
            throw new PaymentException("You didn't specify the beneficiary's account", ErrorCode.ERROR);
        }

        AccountEntity accountEntityFrom = getByUUID(accountFrom);

        if (accountEntityFrom.getDateLastUpd().equals(dtUpd)) {

            checkCurrency(accountEntityFrom.getCurrency(), paymentCurrency);
            BigDecimal balanceFrom = accountEntityFrom.getBalance();
            checkBalance(balanceFrom, sumPayment);

            AccountEntity accountEntityTo = getByUUID(accountTo);
            checkCurrency(accountEntityTo.getCurrency(), paymentCurrency);
            BigDecimal balanceTo = accountEntityTo.getBalance();

            balanceFrom = balanceFrom.subtract(sumPayment);
            balanceTo = balanceTo.add(sumPayment);

            accountEntityFrom.setBalance(balanceFrom);
            accountEntityTo.setBalance(balanceTo);

            repository.saveAll(List.of(accountEntityFrom, accountEntityTo));

            Transaction transaction = Transaction.builder()
                    .setId(UUID.randomUUID())
                    .setCurrency(payment.getCurrency())
                    .setAccountFrom(accountEntityFrom.getNum())
                    .setAccountTo(accountEntityTo.getNum())
                    .setSum(payment.getSum())
                    .setType(TransactionType.TRANSFER)
                    .setDate(LocalDateTime.now())
                    .build();

            publisher.publishEvent(new TransactionEvent(transaction));

            return transaction;
        } else {
            throw new NotFoundDataBaseException("Account with this version wasn't found in the database", ErrorCode.ERROR);
        }
    }

    @Override
    public Account getInfo(@NotNull UUID account) {
        if (!conversionService.canConvert(AccountEntity.class, Account.class)) {
            throw new ConversionTimeException("Unable to convert", ErrorCode.ERROR);
        }

        AccountEntity accountEntity = getByUUID(account);
        return conversionService.convert(accountEntity, Account.class);
    }

    @Override
    public AccountEntity getByUUID(@NotNull UUID account) {
        return repository.findById(account)
                .orElseThrow(() -> new NotFoundDataBaseException("Account wasn't found in the data base", ErrorCode.ERROR));
    }

    private void checkCurrency(Currency accountCurrency, Currency paymentCurrency) {
        if(!Objects.equals(accountCurrency, paymentCurrency)) {
            throw new PaymentException("Account currency does not match the payment currency", ErrorCode.ERROR);
        }
    }

    private void checkBalance(BigDecimal balance, BigDecimal sum) {
        int res = balance.compareTo(sum);
        if (res < 0) {
            throw new PaymentException("You don't have enough money for this operation", ErrorCode.ERROR);
        }
    }
}
