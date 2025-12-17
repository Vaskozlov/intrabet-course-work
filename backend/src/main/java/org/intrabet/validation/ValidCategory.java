package org.intrabet.validation;

import jakarta.validation.Payload;

public @interface ValidCategory {
    String message() default "Category is not present in the database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
