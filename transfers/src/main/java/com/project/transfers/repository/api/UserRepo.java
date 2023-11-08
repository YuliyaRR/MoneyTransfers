package com.project.transfers.repository.api;

import com.project.transfers.core.dto.DocumentType;
import com.project.transfers.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, UUID> {
    @Query(value = "select uuid from app.users where doc_num =:docNum and doc_type =:#{#docType.name()}", nativeQuery = true)
    Optional<UUID> existsUUIDByDocNumAndDocType(String docNum, DocumentType docType);
}
