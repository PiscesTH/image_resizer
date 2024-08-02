package com.th.image_resizer.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidError> validErrorList;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidError {
        private final String field;
        private final String message;

        public static ValidError putError(final FieldError fieldError) {
            return ValidError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
