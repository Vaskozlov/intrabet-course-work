package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.vaskozlov.is.course.bean.Category;
import org.vaskozlov.is.course.bean.Event;
import org.vaskozlov.is.course.bean.EventStatus;

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
