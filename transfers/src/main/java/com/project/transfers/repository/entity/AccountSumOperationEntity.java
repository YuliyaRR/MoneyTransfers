package com.project.transfers.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AccountSumOperationEntity {
    private BigDecimal sum;
    @ManyToOne
    @JoinColumn(name = "account_uuid")
    private AccountEntity accountEntity;

}
