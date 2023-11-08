package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID accountFrom;
    @JsonProperty(value = "account_to")
    private UUID accountTo;
    @NotNull
    private Currency currency;
    @NotNull
    @Positive
    private double sum;
    private TransactionType type;
}
