package org.intrabet.api;

import jakarta.validation.Valid;
import org.intrabet.bean.Category;
import org.intrabet.bean.User;
import org.intrabet.dto.CreatedEventDTO;
import org.intrabet.dto.EventFinishDTO;
import org.intrabet.service.CategoryService;
import org.intrabet.service.EventsService;
import org.intrabet.service.notifications.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/events")
public class EventsController {
    private final EventsService eventsService;
    private final CategoryService categoryService;
    private final EventPublisher eventPublisher;

    @Autowired
    public EventsController(EventsService eventsService,
                            CategoryService categoryService,
                            EventPublisher eventPublisher
    ) {
        this.eventsService = eventsService;
        this.categoryService = categoryService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(
            @Valid @RequestBody CreatedEventDTO eventDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var event = eventsService.createEvent(eventDTO, user.getId());

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

    @PostMapping("/finish")
    public ResponseEntity<?> finishEvent(
            @Valid @RequestBody EventFinishDTO eventFinishDTO,
            @AuthenticationPrincipal User user
    ) {
        try {
            var finishResult = eventsService.finishEvent(eventFinishDTO, user);

            if (finishResult.isError()) {
                return ResponseEntity
                        .badRequest()
                        .body(finishResult.getError());
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

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(60));
        eventPublisher.addEmitter(emitter);
        return emitter;
    }
}
