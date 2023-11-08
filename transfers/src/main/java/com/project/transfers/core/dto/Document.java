package com.project.transfers.core.dto;

import com.project.transfers.validator.ValidString;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @NotNull
    private DocumentType type;
    @ValidString
    private String num;
}
