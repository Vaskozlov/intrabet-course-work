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
import org.vaskozlov.is.course.service.notifications.EventNotificationService;
import org.vaskozlov.is.course.service.notifications.UserNotificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventsService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final OutcomeRepository outcomeRepository;
    private final EventNotificationService eventNotificationService;
    private final UserNotificationService userNotificationService;

    @Autowired
    public EventsService(
            CategoryRepository categoryRepository,
            EventRepository eventRepository,
            OutcomeRepository outcomeRepository,
            EventNotificationService eventNotificationService, UserNotificationService userNotificationService) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.outcomeRepository = outcomeRepository;
        this.eventNotificationService = eventNotificationService;
        this.userNotificationService = userNotificationService;
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

        eventNotificationService.notify(event);
        return event;
    }

    @Cacheable(value = "eventsCache", key = "#category?.id + '_' + #givenTime")
    public List<Event> findEvents(Category category, Instant givenTime) {
        List<EventStatus> excludedStatuses = List.of(EventStatus.CANCELLED, EventStatus.COMPLETED);

        if (category == null && givenTime == null) {
            return eventRepository.findAllWithOutcomesAndCategory();
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

        if (!user.getId().equals(authorId) && !user.getRole().equals(Role.ADMIN)) {
            return Result.error("You are not allowed to finish event");
        }

        if (eventFinishDTO.getStatus().equals(EventStatus.COMPLETED)) {
            assert eventFinishDTO.getOutcomeId() != null;

            var outcome = outcomeRepository
                    .findById(eventFinishDTO.getOutcomeId())
                    .orElseThrow();

            outcome.setIsWinner(true);
            distributeMoney(event);
        } else if (eventFinishDTO.getStatus().equals(EventStatus.CANCELLED)) {
            returnUsersMoney(event);
        }

        event.setStatus(eventFinishDTO.getStatus());
        event.setClosedAt(Instant.now());

        eventNotificationService.notify(event);
        return Result.success(null);
    }

    private void returnUsersMoney(Event event) {
        event
                .getOutcomes()
                .stream()
                .flatMap(outcome -> outcome.getBets().stream())
                .forEach(bet -> {
                            var user = bet.getUser();

                            user.getWallet()
                                    .addBalance(bet.getAmount());

                            userNotificationService.notifyAccountChange(user);
                        }
                );

    }

    private void distributeMoney(Event event) {
        BigDecimal sumFailed = event.getOutcomes()
                .stream()
                .filter(outcome -> !Boolean.TRUE.equals(outcome.getIsWinner()))
                .flatMap(outcome -> outcome.getBets().stream())
                .map(Bet::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Bet> winningBets = event.getOutcomes()
                .stream()
                .filter(outcome -> Boolean.TRUE.equals(outcome.getIsWinner()))
                .flatMap(outcome -> outcome.getBets().stream())
                .toList();

        BigDecimal sumSucceed = winningBets.stream()
                .map(Bet::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumSucceed.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal sumToDistribute = sumFailed.multiply(BigDecimal.valueOf(0.8));
        final BigDecimal[] distributed = {BigDecimal.ZERO};

        winningBets.forEach(bet -> {
            BigDecimal wonMoney = bet.getAmount()
                    .divide(sumSucceed, RoundingMode.FLOOR)
                    .multiply(sumToDistribute)
                    .max(BigDecimal.ZERO);

            User user = bet.getUser();

            user.getWallet()
                    .addBalance(wonMoney);

            distributed[0] = distributed[0].add(wonMoney);
            userNotificationService.notifyAccountChange(user);
        });
    }
}
