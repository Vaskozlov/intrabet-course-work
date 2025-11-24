package org.vaskozlov.is.course.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.vaskozlov.is.course.validation.ValidOutcomeId;

@Data
public class BetDTO {
    @ValidOutcomeId
    private Long outcomeId;

    @Positive
    private Long sum;
}
