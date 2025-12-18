package org.intrabet.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;

@Data
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "outcomeCache")
@Table(
        name = "outcome",
        indexes = {
                @Index(name = "idx_outcome_event_id", columnList = "event_id"),
        }
)
public class Outcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String description;

    private Boolean isWinner = null;

    @JsonManagedReference
    @OneToMany(mappedBy = "outcome", fetch = FetchType.LAZY)
    private List<Bet> bets;
}
