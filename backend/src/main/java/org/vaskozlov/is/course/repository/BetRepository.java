package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Bet;

public interface BetRepository extends JpaRepository<Bet, Long> {
}
