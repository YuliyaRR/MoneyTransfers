package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @JsonProperty(value = "account_from")
    @Schema(description = "Счет-отправитель")
    private UUID accountFrom;
    @JsonProperty(value = "account_to")
    @Schema(description = "Счет-получатель")
    private UUID accountTo;
    @NotNull
    @Schema(description = "Валюта платежа")
    private Currency currency;
    @NotNull
    @Positive
    @Schema(description = "Сумма платежа", example = "52.25")
    private double sum;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Тип операции")
    private TransactionType type;
}
