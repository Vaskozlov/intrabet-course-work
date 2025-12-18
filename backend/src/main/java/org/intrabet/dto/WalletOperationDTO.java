package org.intrabet.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WalletOperationDTO {
    @Positive(message = "Amount must be positive")
    private Long amount;
}
