package com.project.transfers.repository.api;

import com.project.transfers.repository.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepo extends JpaRepository<AccountEntity, UUID> {
}
