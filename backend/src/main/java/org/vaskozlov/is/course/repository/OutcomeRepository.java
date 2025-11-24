package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Outcome;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {

}
