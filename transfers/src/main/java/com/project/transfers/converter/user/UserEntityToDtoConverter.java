package com.project.transfers.converter.user;

import com.project.transfers.core.dto.Document;
import com.project.transfers.core.dto.User;
import com.project.transfers.repository.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToDtoConverter implements Converter <UserEntity, User> {
    @Override
    public User convert(UserEntity source) {

        return User.builder()
                .setId(source.getUuid())
                .setName(source.getName())
                .setDocument(new Document(source.getDoc().getDocType(), source.getDoc().getDocNum()))
                .setDateRegistration(source.getDateRegistration())
                .setDateLastUpd(source.getDateLastUpd())
                .build();
    }
}
