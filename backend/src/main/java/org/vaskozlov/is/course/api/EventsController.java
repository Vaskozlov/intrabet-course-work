package org.vaskozlov.is.course.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vaskozlov.is.course.bean.Category;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.dto.CreatedEventDTO;
import org.vaskozlov.is.course.service.CategoryService;
import org.vaskozlov.is.course.service.EventsService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {
    private final EventsService eventsService;
    private final CategoryService categoryService;

    @Autowired
    public EventsController(EventsService eventsService, CategoryService categoryService) {
        this.eventsService = eventsService;
        this.categoryService = categoryService;
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
    public ResponseEntity<?> listEvents(
            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            ZonedDateTime date
    ) {
        Instant time = null;

        if (date != null) {
            time = date.toInstant();
        }

        Category eventCategory = null;

        if (category != null) {
            eventCategory = categoryService
                    .findByName(category)
                    .orElse(null);

            if (eventCategory == null) {
                return ResponseEntity
                        .badRequest()
                        .body("Category not found");
            }
        }

        try {
            List<?> events = eventsService.findEvents(eventCategory, time);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
