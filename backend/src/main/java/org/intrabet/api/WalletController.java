package org.intrabet.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.intrabet.bean.User;
import org.intrabet.dto.WalletOperationDTO;
import org.intrabet.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @Valid @RequestBody WalletOperationDTO operationDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var result = walletService.deposit(operationDTO, user);

            if (result.isError()) {
                return ResponseEntity
                        .badRequest()
                        .body(result.getError());
            }

            return ResponseEntity
                    .ok()
                    .build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WalletOperationDTO operationDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var result = walletService.withdraw(operationDTO, user);

            if (result.isError()) {
                return ResponseEntity
                        .badRequest()
                        .body(result.getError());
            }

            return ResponseEntity
                    .ok()
                    .build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
