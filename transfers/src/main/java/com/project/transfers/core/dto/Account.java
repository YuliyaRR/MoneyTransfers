package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.transfers.converter.date.LocalDateTimeToLongMillisSerializer;
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
    private UUID uuid;
    private Currency currency;
    @PositiveOrZero
    private BigDecimal balance;
    @JsonProperty(value = "dt_open")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    private LocalDateTime dateOpen;
    @JsonProperty(value = "dt_upd")
    @JsonSerialize(using = LocalDateTimeToLongMillisSerializer.class)
    private LocalDateTime dateLastUpd;
    private User owner;
}
