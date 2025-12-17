package org.intrabet.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.intrabet.bean.User;
import org.intrabet.dto.BetDTO;
import org.intrabet.service.BetService;

@RestController
@RequestMapping("/bets")
public class BetsController {
    private final BetService betService;

    public BetsController(BetService betService) {
        this.betService = betService;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeBet(
            @Valid @RequestBody BetDTO betDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var placeResult = betService.place(betDTO, user);

            if (placeResult.isError()) {
                return ResponseEntity
                        .badRequest()
                        .body(placeResult.getError());
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getBetsList(
            @RequestParam(required = false, defaultValue = "false")
            boolean showClosed,

            @AuthenticationPrincipal
            User user
    ) {
        try {
            return ResponseEntity
                    .ok()
                    .body(betService.getBets(user, showClosed));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
