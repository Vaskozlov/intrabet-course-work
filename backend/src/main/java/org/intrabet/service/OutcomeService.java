package org.intrabet.service;

import org.springframework.stereotype.Service;
import org.intrabet.bean.Event;
import org.intrabet.bean.Outcome;
import org.intrabet.repository.OutcomeRepository;

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
