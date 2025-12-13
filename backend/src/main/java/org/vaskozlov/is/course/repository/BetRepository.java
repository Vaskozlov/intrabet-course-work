package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Bet;
import org.vaskozlov.is.course.bean.EventStatus;
import org.vaskozlov.is.course.bean.User;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user);

    List<Bet> findByUserAndOutcomeEventStatusIn(User user, List<EventStatus> eventStatuses);
}
