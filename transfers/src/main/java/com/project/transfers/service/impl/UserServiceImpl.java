package com.project.transfers.service.impl;

import com.project.transfers.core.dto.Document;
import com.project.transfers.core.dto.DocumentType;
import com.project.transfers.core.dto.User;
import com.project.transfers.core.error.ErrorCode;
import com.project.transfers.core.exceptions.ConversionTimeException;
import com.project.transfers.core.exceptions.InvalidInputServiceSingleException;
import com.project.transfers.core.exceptions.NotFoundDataBaseException;
import com.project.transfers.repository.api.UserRepo;
import com.project.transfers.repository.entity.DocumentEntity;
import com.project.transfers.repository.entity.UserEntity;
import com.project.transfers.service.api.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepo repository;
    private final ConversionService conversionService;

    @Override
    @Transactional
    public void create(@NotNull @Valid User user) {
        Document document = user.getDocument();

        if(doesUserExistByDoc(document.getType(), document.getNum())) {
            throw new InvalidInputServiceSingleException("This user is already registered", ErrorCode.ERROR);
        }

        if (!conversionService.canConvert(User.class, UserEntity.class)) {
            throw new ConversionTimeException("Unable to convert", ErrorCode.ERROR);
        }

        UserEntity entity = conversionService.convert(user, UserEntity.class);

        repository.save(entity);
    }

    @Override
    public User getInfo(@NotNull UUID uuid) {
        if (!conversionService.canConvert(UserEntity.class, User.class)) {
            throw new ConversionTimeException("Unable to convert", ErrorCode.ERROR);
        }

        UserEntity userEntity = getByUUID(uuid);

        return conversionService.convert(userEntity, User.class);
    }

    @Override
    @Transactional
    public void update(@NotNull UUID uuid, @NotNull @Valid User user, @NotNull @Past LocalDateTime dtUpd) {
        UserEntity entity = getByUUID(uuid);

        if (entity.getDateLastUpd().equals(dtUpd)) {
            entity.setName(user.getName());

            Document document = user.getDocument();
            DocumentEntity entityDoc = entity.getDoc();

            entityDoc.setDocNum(document.getNum());
            entityDoc.setDocType(document.getType());

            repository.save(entity);

        } else {
            throw new NotFoundDataBaseException("User with this version wasn't found in the database", ErrorCode.ERROR);
        }
    }

    @Override
    public void delete(@NotNull UUID uuid) {
        repository.delete(getByUUID(uuid));
    }

    @Override
    public UserEntity getByUUID(@NotNull UUID uuid) {
        return repository.findById(uuid)
                .orElseThrow(() -> new NotFoundDataBaseException("The user wasn't found in the database", ErrorCode.ERROR));
    }

    @Override
    public boolean doesUserExistByUUID(@NotNull UUID uuid) {
        try {
            getByUUID(uuid);
            return true;
        } catch (NotFoundDataBaseException e) {
            return false;
        }
    }

    private boolean doesUserExistByDoc(DocumentType type, String docNum) {
        Optional<UUID> optional = repository.existsUUIDByDocNumAndDocType(docNum, type);
        return optional.isPresent();
    }


}
