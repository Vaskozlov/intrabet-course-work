package org.vaskozlov.is.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Category;
import org.vaskozlov.is.course.bean.Event;
import org.vaskozlov.is.course.bean.Outcome;
import org.vaskozlov.is.course.dto.CreatedEventDTO;
import org.vaskozlov.is.course.dto.CreatedOutcomeDTO;
import org.vaskozlov.is.course.repository.CategoryRepository;
import org.vaskozlov.is.course.repository.EventRepository;
import org.vaskozlov.is.course.repository.OutcomeRepository;

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

    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}
