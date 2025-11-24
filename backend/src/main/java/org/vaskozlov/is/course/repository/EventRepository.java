package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
