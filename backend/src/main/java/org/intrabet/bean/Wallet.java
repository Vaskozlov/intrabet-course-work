package org.intrabet.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "wallet",
        indexes = {
                @Index(name = "idx_wallet_user_id", columnList = "user_id")
        }
)

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Digits(integer = 18, fraction = 2)
    @Column(precision = 20, scale = 2)
    @PositiveOrZero(message = "Balance can not be negative")
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private Currency currency = Currency.RUB;

    @NotNull
    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void addBalance(BigDecimal balanceChange) {
        balance = balance.add(balanceChange);
    }
}
