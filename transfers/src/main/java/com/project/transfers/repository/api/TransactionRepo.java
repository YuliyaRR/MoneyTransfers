package com.project.transfers.repository.api;

import com.project.transfers.repository.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepo extends JpaRepository <TransactionEntity, UUID> {
   List<TransactionEntity> findAllByAccountSumOperation_AccountEntity_Num(UUID accountNum);
}
