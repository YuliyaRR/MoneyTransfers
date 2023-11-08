package com.project.transfers.core.error;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSingleError {
    private ErrorCode logref;
    private String message;
}
