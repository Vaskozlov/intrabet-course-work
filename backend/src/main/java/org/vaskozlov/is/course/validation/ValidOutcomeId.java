package org.vaskozlov.is.course.validation;

import jakarta.validation.Payload;

public @interface ValidOutcomeId {
    String message() default "Given outcome does not exist or the event has been finished";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
