package org.intrabet.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.intrabet.bean.EventStatus;

@Data
public class EventFinishDTO {
    // add @ValidEventId
    @NotNull
    private Long eventId;

    @NotNull
    private EventStatus status;

    @Nullable
    private Long outcomeId;
}
