package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.transfers.converter.date.LocalDateTimeToLongMillisSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder(setterPrefix = "set")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Номер счета")
    private UUID uuid;
    @Schema(description = "Валюта счета")
    private Currency currency;
    @PositiveOrZero
    @Schema(description = "Баланс счета", example = "52.25")
    private BigDecimal balance;
    @JsonProperty(value = "dt_open")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата открытия счета")
    private LocalDateTime dateOpen;
    @JsonProperty(value = "dt_upd")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата последнего движения по счету")
    private LocalDateTime dateLastUpd;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Владелец счета")
    private User owner;
}
