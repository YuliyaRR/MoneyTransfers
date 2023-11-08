package com.project.transfers.repository.entity;

import com.project.transfers.core.dto.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts", schema = "app")
public class AccountEntity {
    @Id
    private UUID num;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal balance;
    @Column(name = "date_open")
    private LocalDateTime dateOpen;
    @Version
    @Column(name = "date_upd")
    private LocalDateTime dateLastUpd;
    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;
}
