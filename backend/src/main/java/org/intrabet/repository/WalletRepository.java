package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.intrabet.bean.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
