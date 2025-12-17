package org.intrabet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.intrabet.bean.Bet;

@Data
@AllArgsConstructor
public class PlacedBetDto {
    private Long eventId;

    private Long outcomeId;

    public static PlacedBetDto fromBet(Bet bet) {
        var outcome = bet.getOutcome();
        var event = outcome.getEvent();

        return new PlacedBetDto(event.getId(), outcome.getId());
    }
}
