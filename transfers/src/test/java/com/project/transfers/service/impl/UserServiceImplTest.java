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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private ConversionService conversionService;
    private IUserService userService;
    private User user;
    private UserEntity entity;
    private UUID userUUID;
    private final static String EXCEPTION_USER_IS_ALREADY_REGISTERED = "This user is already registered";
    private final static String EXCEPTION_UNABLE_TO_CONVERT = "Unable to convert";
    private final static String EXCEPTION_USER_NOT_FOUND = "The user wasn't found in the database";
    private final static String EXCEPTION_USER_NOT_FOUND_VERSION = "User with this version wasn't found in the database";

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepo, conversionService);
        this.user = User.builder()
                .setName("Test User")
                .setDocument(new Document(DocumentType.PASSPORT, "123"))
                .build();

        LocalDateTime date = LocalDateTime.of(2020,10, 13, 14, 15,13);
        this.userUUID = UUID.randomUUID();

        this.entity = UserEntity.builder()
                .setUuid(userUUID)
                .setName(user.getName())
                .setDoc(new DocumentEntity(user.getDocument().getType(), user.getDocument().getNum()))
                .setDateRegistration(date)
                .setDateLastUpd(date)
                .build();
    }
    @Test
    public void createUserWhenAllDataIsValidAndUserWasNotAlreadyRegisteredThenCreateUser() {
        when(conversionService.canConvert(User.class, UserEntity.class)).thenReturn(true);
        when(conversionService.convert(user, UserEntity.class)).thenReturn(entity);

        userService.create(user);
        verify(userRepo, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void createUserWhenUserIsAlreadyRegisteredThenThrowException() {
        when(userRepo.existsUUIDByDocNumAndDocType(any(String.class), any(DocumentType.class))).thenReturn(Optional.of(UUID.randomUUID()));

        InvalidInputServiceSingleException exception = assertThrows(InvalidInputServiceSingleException.class, () -> userService.create(user));
        assertEquals(EXCEPTION_USER_IS_ALREADY_REGISTERED, exception.getMessage());

        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    public void createUserWhenConverterNotFoundThenThrowException() {
        when(conversionService.canConvert(User.class, UserEntity.class)).thenReturn(false);

        ConversionTimeException exception = assertThrows(ConversionTimeException.class, () -> userService.create(user));
        assertEquals(EXCEPTION_UNABLE_TO_CONVERT, exception.getMessage());

        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    public void getInfoWhenUserWasFoundThenReturnedIt() {
        User expected = User.builder()
                .setId(entity.getUuid())
                .setName(entity.getName())
                .setDocument(new Document(entity.getDoc().getDocType(), entity.getDoc().getDocNum()))
                .setDateRegistration(entity.getDateRegistration())
                .setDateLastUpd(entity.getDateLastUpd())
                .build();
        when(conversionService.canConvert(UserEntity.class, User.class)).thenReturn(true);
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));
        when(conversionService.convert(entity, User.class)).thenReturn(expected);

        User info = userService.getInfo(userUUID);

        assertNotNull(info);
        assertEquals(expected, info);

        verify(userRepo, times(1)).findById(userUUID);
    }

    @Test
    public void getInfoWhenConverterNotFoundThenThrowException() {
        when(conversionService.canConvert(UserEntity.class, User.class)).thenReturn(false);

        ConversionTimeException exception = assertThrows(ConversionTimeException.class, () -> userService.getInfo(userUUID));
        assertEquals(EXCEPTION_UNABLE_TO_CONVERT, exception.getMessage());

        verify(userRepo, never()).findById(userUUID);
    }

    @Test
    public void getInfoWhenUserNotFoundThenThrowException() {
        when(conversionService.canConvert(UserEntity.class, User.class)).thenReturn(true);
        when(userRepo.findById(userUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> userService.getInfo(userUUID));
        assertEquals(EXCEPTION_USER_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());


        verify(userRepo, times(1)).findById(userUUID);
        verify(conversionService, never()).convert(any(UserEntity.class), eq(User.class));
    }

    @Test
    public void updateWhenDataIsValidThenUpdateUser() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));

        userService.update(userUUID, user, entity.getDateLastUpd());

        verify(userRepo, times(1)).save(entity);
    }

    @Test
    public void updateWhenVersionNotValidThenThrowException() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> userService.update(userUUID, user, LocalDateTime.now()));
        assertEquals(EXCEPTION_USER_NOT_FOUND_VERSION, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(userRepo, never()).save(entity);
    }

    @Test
    public void updateWhenUserNotFoundThenThrowException() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> userService.update(userUUID, user, entity.getDateLastUpd()));
        assertEquals(EXCEPTION_USER_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(userRepo, never()).save(entity);
    }

    @Test
    public void deleteWhenUserWasFoundThenDeleteIt() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));

        userService.delete(userUUID);

        verify(userRepo, times(1)).delete(entity);
    }

    @Test
    public void deleteWhenUserNotFoundThenThrowException() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> userService.delete(userUUID));
        assertEquals(EXCEPTION_USER_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());

        verify(userRepo, never()).delete(any(UserEntity.class));
    }

    @Test
    public void getByUUIDWhenUserWasFoundThenEntityWasReturn() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));

        assertNotNull(userService.getByUUID(userUUID));
    }

    @Test
    public void getByUUIDWhenUserNotFoundThenThrowException() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.empty());

        NotFoundDataBaseException exception = assertThrows(NotFoundDataBaseException.class, () -> userService.getByUUID(userUUID));
        assertEquals(EXCEPTION_USER_NOT_FOUND, exception.getMessage());
        assertEquals(ErrorCode.ERROR, exception.getErrorCode());
    }

    @Test
    public void doesUserExistByUUIDWhenUserExistThenReturnTrue() {
        when(userRepo.findById(userUUID)).thenReturn(Optional.of(entity));

        assertTrue(userService.doesUserExistByUUID(userUUID));
    }

    @Test
    public void doesUserExistByUUIDWhenUserNotExistThenReturnFalse() {
        when(userRepo.findById(userUUID)).thenThrow(NotFoundDataBaseException.class);

        assertFalse(userService.doesUserExistByUUID(userUUID));
    }
}