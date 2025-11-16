package org.vaskozlov.is.course.bean;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Event implements Serializable {
    @Id
    @JsonbNillable
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @JsonbNillable
    private Instant startsAt;

    @JsonbNillable
    private Instant endsAt;

    @AssertTrue(message = "endsAt must not be before startsAt")
    private boolean isValidDateRange() {
        // Return true if null (validation passes; use @NotNull for required check)
        return startsAt == null || endsAt == null || !endsAt.isBefore(startsAt);
    }

    @NotNull
    EventStatus status = EventStatus.PLANNED;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "events")
    private List<User> users = new ArrayList<>();
}
