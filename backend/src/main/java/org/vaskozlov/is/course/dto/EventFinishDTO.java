package org.vaskozlov.is.course.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.vaskozlov.is.course.bean.EventStatus;

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
