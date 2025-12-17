package org.intrabet.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.intrabet.validation.ValidOutcomeId;

@Data
public class BetDTO {
    @ValidOutcomeId
    private Long outcomeId;

    @Positive
    private Long sum;
}
