package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.intrabet.bean.Bet;
import org.intrabet.bean.EventStatus;
import org.intrabet.bean.User;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user);

    List<Bet> findByUserAndOutcomeEventStatusIn(User user, List<EventStatus> eventStatuses);
}
