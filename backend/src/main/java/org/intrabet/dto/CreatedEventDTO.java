package org.intrabet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.intrabet.validation.ValidCategory;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class CreatedEventDTO {
    @NotBlank(message = "Event name is required")
    private String title;

    private String description;

    @ValidCategory
    private String category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @FutureOrPresent(message = "Start date must not be in the past")
    private ZonedDateTime startsAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @FutureOrPresent(message = "End date must not be in the past")
    private ZonedDateTime endsAt;

    @Size(min = 2, message = "At least two outcomes are required for the event")
    private List<CreatedOutcomeDTO> createdOutcomes;

    @AssertTrue(message = "End date must be after start date")
    private boolean isEndAfterStart() {
        if (startsAt == null || endsAt == null) {
            return true;
        }

        return endsAt.isAfter(startsAt);
    }
}
