package org.vaskozlov.is.course.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="wallet_id", nullable = false, unique = true)
    private Wallet wallet;

    @NotNull
    @Digits(integer = 18, fraction = 2)
    @Column(precision = 20, scale = 2)
    private BigDecimal amount;

    @NotNull
    TransactionType transactionType;

    @CreationTimestamp
    Instant transactionDate;
}
