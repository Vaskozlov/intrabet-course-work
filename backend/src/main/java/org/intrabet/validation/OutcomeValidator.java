package org.intrabet.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.intrabet.bean.Event;
import org.intrabet.bean.EventStatus;
import org.intrabet.service.OutcomeService;

@Component
public class OutcomeValidator implements ConstraintValidator<ValidOutcomeId, Long> {
    private final OutcomeService outcomeService;

    @Autowired
    public OutcomeValidator(OutcomeService outcomeService) {
        this.outcomeService = outcomeService;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return outcomeService
                .getEventById(value)
                .map(Event::getStatus)
                .map(eventStatus -> eventStatus.equals(EventStatus.PLANNED))
                .orElse(false);
    }
}
