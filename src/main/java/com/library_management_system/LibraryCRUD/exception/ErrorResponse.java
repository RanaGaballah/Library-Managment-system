package com.library_management_system.LibraryCRUD.exception;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ErrorResponse {

    private String message;
    private List<FieldError> fieldErrors;

    public ErrorResponse(String message, List<FieldError> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
