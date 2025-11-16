package org.vaskozlov.is.course.bean;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.vaskozlov.is.course.service.PasswordHasher;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "application_users")
@NoArgsConstructor
public class User implements Serializable {
    @Builder
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.passwordHash = PasswordHasher.hashPassword(password.toCharArray());
    }

    @Id
    @JsonbNillable
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    @JsonbTransient
    private String passwordHash;

    @NotNull
    private Role role = Role.STUDENT;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_event", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events = new ArrayList<>();
}
