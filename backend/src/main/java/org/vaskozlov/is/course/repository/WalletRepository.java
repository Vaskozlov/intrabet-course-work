package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
