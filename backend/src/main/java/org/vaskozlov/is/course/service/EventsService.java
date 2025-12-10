package org.vaskozlov.is.course.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Category;
import org.vaskozlov.is.course.bean.Event;
import org.vaskozlov.is.course.bean.EventStatus;
import org.vaskozlov.is.course.bean.Outcome;
import org.vaskozlov.is.course.dto.CreatedEventDTO;
import org.vaskozlov.is.course.dto.CreatedOutcomeDTO;
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
    public Event createEvent(CreatedEventDTO createdEventDTO) {
        Event event = new Event();

        event.setTitle(createdEventDTO.getTitle());
        event.setDescription(createdEventDTO.getDescription());

        event.setStartsAt(createdEventDTO.getStartsAt().toInstant());
        event.setEndsAt(createdEventDTO.getEndsAt().toInstant());

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
}
