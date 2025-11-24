package org.vaskozlov.is.course.service;

import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Event;
import org.vaskozlov.is.course.bean.Outcome;
import org.vaskozlov.is.course.repository.OutcomeRepository;

import java.util.Optional;

@Service
public class OutcomeService {
    private final OutcomeRepository outcomeRepository;

    public OutcomeService(OutcomeRepository outcomeRepository) {
        this.outcomeRepository = outcomeRepository;
    }

    public Optional<Event> getEventById(Long id) {
        return outcomeRepository
                .findById(id)
                .map(Outcome::getEvent);
    }
}
