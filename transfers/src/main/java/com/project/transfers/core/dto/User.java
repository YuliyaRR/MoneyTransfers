package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.transfers.converter.date.LocalDateTimeToLongMillisSerializer;
import com.project.transfers.validator.ValidString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(setterPrefix = "set")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Include
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Идентификатор пользователя")
    private UUID id;
    @ValidString
    @Schema(description = "Имя пользователя")
    private String name;
    @Valid
    @Schema(description = "Документ пользователя")
    private Document document;
    @JsonProperty(value = "dt_reg")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата регистрации пользователя")
    private LocalDateTime dateRegistration;
    @JsonProperty(value = "dt_upd")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата последнего обновления данных пользователя")
    private LocalDateTime dateLastUpd;

    public User(UUID id) {
        this.id = id;
    }
}
