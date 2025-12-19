package org.intrabet.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.intrabet.bean.User;
import org.intrabet.dto.WalletOperationDTO;
import org.intrabet.lib.Result;
import org.intrabet.repository.WalletRepository;
import org.intrabet.service.notifications.UserPublisher;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserPublisher UserPublisher;

    @Autowired
    public WalletService(WalletRepository walletRepository, UserPublisher userPublisher) {
        this.walletRepository = walletRepository;
        this.UserPublisher = userPublisher;
    }

    @Transactional
    public Result<Void, String> deposit(WalletOperationDTO operationDTO, User user) {
        var wallet = user.getWallet();
        var amount = BigDecimal.valueOf(operationDTO.getAmount());

        wallet.addBalance(amount);
        walletRepository.save(wallet);
        UserPublisher.notifyAccountChange(user);

        return Result.success(null);
    }

    @Transactional
    public Result<Void, String> withdraw(WalletOperationDTO operationDTO, User user) {
        var wallet = user.getWallet();
        var amount = BigDecimal.valueOf(operationDTO.getAmount());

        if (wallet.getBalance().compareTo(amount) < 0) {
            return Result.error("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        UserPublisher.notifyAccountChange(user);

        return Result.success(null);
    }
}
