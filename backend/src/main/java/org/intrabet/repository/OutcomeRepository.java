package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.intrabet.bean.Outcome;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {

}
