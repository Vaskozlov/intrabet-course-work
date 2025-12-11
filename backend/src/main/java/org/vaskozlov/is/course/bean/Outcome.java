package org.vaskozlov.is.course.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Outcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String description;

    private Boolean isWinner = null;

    @OneToMany(mappedBy = "outcome", fetch = FetchType.LAZY)
    private List<Bet> bets;
}
