package com.project.transfers.core.dto;

import com.fasterxml.jackson.annotation.*;
import com.project.transfers.core.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder(setterPrefix = "set")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Идентификатор транзакции")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private UUID id;
    @Schema(description = "Валюта операции")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private Currency currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Дата операции")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private LocalDateTime date;
    @JsonProperty(value = "account_from")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Счет-отправитель")
    @JsonView({Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private UUID accountFrom;
    @JsonProperty(value = "account_to")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Счет-получатель")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutTransfer.class})
    private UUID accountTo;
    @Schema(description = "Сумма операции", example = "52.25")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private double sum;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Тип операции")
    @JsonView({Views.transactionalOutAccountTo.class, Views.transactionalOutAccountFrom.class, Views.transactionalOutTransfer.class})
    private TransactionType type;
}