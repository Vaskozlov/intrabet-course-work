package org.vaskozlov.is.course.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.dto.CreatedEventDTO;
import org.vaskozlov.is.course.service.EventsService;

@RestController
@RequestMapping("/events")
public class EventsController {
    private final EventsService eventsService;

    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(
            @Valid @RequestBody CreatedEventDTO eventDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var event = eventsService.createEvent(eventDTO);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(event);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listEvents() {
        return ResponseEntity.ok(eventsService.findAll());
    }
}
