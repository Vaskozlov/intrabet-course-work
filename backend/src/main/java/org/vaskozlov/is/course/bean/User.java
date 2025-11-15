package org.vaskozlov.is.course.bean;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @JsonbNillable
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    @JsonbTransient
    private HashType hashType;

    @NotNull
    @JsonbTransient
    private String passwordHash;

    @NotNull
    private Role role;

    @NotNull
    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_event", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn("event_id"))
    private List<Event> events = new ArrayList<>();
}
