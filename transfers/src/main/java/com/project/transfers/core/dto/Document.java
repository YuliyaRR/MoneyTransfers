package com.project.transfers.core.dto;

import com.project.transfers.validator.ValidString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @NotNull
    @Schema(description = "Тип документа пользователя")
    private DocumentType type;
    @ValidString
    @Schema(description = "Номер документа пользователя", example = "AB123456")
    private String num;
}
