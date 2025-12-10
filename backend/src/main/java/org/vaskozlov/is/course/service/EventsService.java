package org.vaskozlov.is.course.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.*;
import org.vaskozlov.is.course.dto.CreatedEventDTO;
import org.vaskozlov.is.course.dto.CreatedOutcomeDTO;
import org.vaskozlov.is.course.dto.EventFinishDTO;
import org.vaskozlov.is.course.lib.Result;
import org.vaskozlov.is.course.repository.CategoryRepository;
import org.vaskozlov.is.course.repository.EventRepository;
import org.vaskozlov.is.course.repository.OutcomeRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventsService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final OutcomeRepository outcomeRepository;

    @Autowired
    public EventsService(
            CategoryRepository categoryRepository,
            EventRepository eventRepository,
            OutcomeRepository outcomeRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.outcomeRepository = outcomeRepository;
    }

    @Transactional
    @CacheEvict(value = "eventsCache", allEntries = true)
    public Event createEvent(CreatedEventDTO createdEventDTO, User author) {
        Event event = new Event();

        event.setTitle(createdEventDTO.getTitle());
        event.setDescription(createdEventDTO.getDescription());

        event.setStartsAt(createdEventDTO.getStartsAt().toInstant());
        event.setEndsAt(createdEventDTO.getEndsAt().toInstant());

        event.setAuthor(author);

        String categoryName = createdEventDTO.getCategory();

        Category category = null;

        if (!categoryName.isBlank()) {
            category = categoryRepository
                    .findByName(categoryName)
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
        }

        event.setCategory(category);
        event = eventRepository.save(event);

        for (CreatedOutcomeDTO outcomeDTO : createdEventDTO.getCreatedOutcomes()) {
            Outcome outcome = new Outcome();
            outcome.setEvent(event);
            outcome.setDescription(outcomeDTO.getDescription());
            outcomeRepository.save(outcome);
        }

        return event;
    }

    @Cacheable(value = "eventsCache", key = "#category?.id + '_' + #givenTime")
    public List<Event> findEvents(Category category, Instant givenTime) {
        List<EventStatus> excludedStatuses = List.of(EventStatus.CANCELLED, EventStatus.COMPLETED);

        if (category == null && givenTime == null) {
            return eventRepository.findAll();
        }

        if (category == null) {
            return eventRepository.findByStatusNotInAndEndsAtGreaterThan(excludedStatuses, givenTime);
        }

        if (givenTime == null) {
            return eventRepository.findByCategory(category);
        }

        return eventRepository.findByCategoryAndStatusNotInAndEndsAtGreaterThan(category, excludedStatuses, givenTime);
    }

    @Cacheable(value = "eventsCache")
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "eventsCache", allEntries = true)
    public Result<Void, String> finishEvent(EventFinishDTO eventFinishDTO, User user) {
        var event = eventRepository
                .findById(eventFinishDTO.getEventId())
                .orElseThrow();

        var authorId = event.getAuthor().getId();

        if (!user.getId().equals(authorId) || !user.getRole().equals(Role.ADMIN)) {
            return Result.error("You are not allowed to finish event");
        }

        event.setStatus(eventFinishDTO.getStatus());
        event.setClosedAt(Instant.now());

        if (event.getStatus().equals(EventStatus.COMPLETED)) {
            assert eventFinishDTO.getOutcomeId() != null;

            var outcome = outcomeRepository
                    .findById(eventFinishDTO.getOutcomeId())
                    .orElseThrow();

            outcome.setIsWinner(true);
        }

        return Result.success(null);
    }
}
