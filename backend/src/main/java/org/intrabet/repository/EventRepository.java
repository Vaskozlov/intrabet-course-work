package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.intrabet.bean.Category;
import org.intrabet.bean.Event;
import org.intrabet.bean.EventStatus;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(Category category);

    List<Event> findByStatusNotInAndEndsAtGreaterThan(List<EventStatus> statuses, Instant endsAt);

    List<Event> findByCategoryAndStatusNotInAndEndsAtGreaterThan(Category category, List<EventStatus> statuses, Instant endsAt);

    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.outcomes " +
           "LEFT JOIN FETCH e.category")
    List<Event> findAllWithOutcomesAndCategory();
}
