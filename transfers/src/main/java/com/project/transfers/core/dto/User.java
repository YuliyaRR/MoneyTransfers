package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.transfers.converter.date.LocalDateTimeToLongMillisSerializer;
import com.project.transfers.validator.ValidString;
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
    private UUID id;
    @ValidString
    private String name;
    @Valid
    private Document document;
    @JsonProperty(value = "dt_reg")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    private LocalDateTime dateRegistration;
    @JsonProperty(value = "dt_upd")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    private LocalDateTime dateLastUpd;

    public User(UUID id) {
        this.id = id;
    }
}
