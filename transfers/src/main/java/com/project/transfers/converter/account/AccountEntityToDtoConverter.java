package com.project.transfers.converter.account;

import com.project.transfers.core.dto.Account;
import com.project.transfers.core.dto.Document;
import com.project.transfers.core.dto.User;
import com.project.transfers.repository.entity.AccountEntity;
import com.project.transfers.repository.entity.DocumentEntity;
import com.project.transfers.repository.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class AccountEntityToDtoConverter implements Converter<AccountEntity, Account> {


    @Override
    public Account convert(AccountEntity source) {

        UserEntity owner = source.getOwner();
        DocumentEntity doc = owner.getDoc();
        Document document = new Document(doc.getDocType(), doc.getDocNum());

        User user = User.builder()
                .setId(owner.getUuid())
                .setName(owner.getName())
                .setDocument(document)
                .setDateRegistration(owner.getDateRegistration())
                .setDateLastUpd(owner.getDateLastUpd())
                .build();

        return Account.builder()
                .setUuid(source.getNum())
                .setCurrency(source.getCurrency())
                .setBalance(source.getBalance())
                .setDateOpen(source.getDateOpen())
                .setDateLastUpd(source.getDateLastUpd())
                .setOwner(user)
                .build();
    }
}
