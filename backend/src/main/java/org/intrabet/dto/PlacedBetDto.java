package org.intrabet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.intrabet.bean.Bet;
import org.intrabet.bean.EventStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class PlacedBetDto {
    private Long eventId;

    private Long outcomeId;

    private Long betId;

    private BigDecimal amount;

    Instant createdAt;

    EventStatus status;

    public static PlacedBetDto fromBet(Bet bet) {
        var outcome = bet.getOutcome();
        var event = outcome.getEvent();

        return new PlacedBetDto(event.getId(),
                outcome.getId(),
                bet.getId(),
                bet.getAmount(),
                bet.getCreatedAt(),
                event.getStatus()
        );
    }
}
