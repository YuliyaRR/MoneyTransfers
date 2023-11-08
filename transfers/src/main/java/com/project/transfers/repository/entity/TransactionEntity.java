package com.project.transfers.repository.entity;

import com.project.transfers.core.dto.Currency;
import com.project.transfers.core.dto.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder(setterPrefix = "set")
@Entity
@Table (name = "transactions", schema = "app")
public class TransactionEntity {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private LocalDateTime date;
    @ElementCollection
    @CollectionTable(name = "accounts-transactions", schema = "app",
    joinColumns = @JoinColumn(name = "transaction_id"))
    private List<AccountSumOperationEntity> accountSumOperation;
}
