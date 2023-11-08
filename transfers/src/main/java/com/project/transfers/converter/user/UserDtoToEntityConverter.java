package com.project.transfers.converter.user;

import com.project.transfers.core.dto.User;
import com.project.transfers.repository.entity.DocumentEntity;
import com.project.transfers.repository.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserDtoToEntityConverter implements Converter<User, UserEntity> {

    @Override
    public UserEntity convert(User source) {
        LocalDateTime date = LocalDateTime.now();

        return UserEntity.builder()
                .setUuid(UUID.randomUUID())
                .setName(source.getName())
                .setDoc(new DocumentEntity(source.getDocument().getType(), source.getDocument().getNum()))
                .setDateRegistration(date)
                .setDateLastUpd(date)
                .build();
    }
}
