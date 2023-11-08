package com.project.transfers.converter.account;

import com.project.transfers.core.dto.Account;
import com.project.transfers.repository.entity.AccountEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AccountDtoToEntityConverter implements Converter<Account, AccountEntity> {
    @Override
    public AccountEntity convert(Account source) {
        LocalDateTime dateTime = LocalDateTime.now();

        return AccountEntity.builder()
                .setNum(UUID.randomUUID())
                .setCurrency(source.getCurrency())
                .setBalance(source.getBalance())
                .setDateOpen(dateTime)
                .setDateLastUpd(dateTime)
                .build();
    }
}
