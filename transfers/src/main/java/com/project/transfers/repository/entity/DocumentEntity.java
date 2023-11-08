package com.project.transfers.repository.entity;

import com.project.transfers.core.dto.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class DocumentEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type")
    private DocumentType docType;
    @Column(name = "doc_num")
    private String docNum;
}
