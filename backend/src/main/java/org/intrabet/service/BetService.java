package org.intrabet.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.intrabet.bean.Bet;
import org.intrabet.bean.EventStatus;
import org.intrabet.bean.User;
import org.intrabet.dto.BetDTO;
import org.intrabet.dto.PlacedBetDto;
import org.intrabet.lib.Result;
import org.intrabet.repository.BetRepository;
import org.intrabet.repository.OutcomeRepository;
import org.intrabet.repository.WalletRepository;
import org.intrabet.service.notifications.UserPublisher;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BetService {
    private final OutcomeRepository outcomeRepository;
    private final BetRepository betRepository;
    private final WalletRepository walletRepository;
    private final UserPublisher userPublisher;

    @Autowired
    public BetService(OutcomeRepository outcomeRepository, BetRepository betRepository, WalletRepository walletRepository, UserPublisher userPublisher) {
        this.outcomeRepository = outcomeRepository;
        this.betRepository = betRepository;
        this.walletRepository = walletRepository;
        this.userPublisher = userPublisher;
    }

    @Transactional
    public Result<Bet, String> place(BetDTO betDTO, User user) {
        var wallet = user.getWallet();
        var sum = BigDecimal.valueOf(betDTO.getSum());

        if (wallet.getBalance().compareTo(sum) < 0) {
            return Result.error("Not enough balance");
        }

        var outcome = outcomeRepository
                .findById(betDTO.getOutcomeId())
                .orElseThrow();

        Bet bet = new Bet();

        bet.setAmount(sum);
        bet.setUser(user);
        bet.setOutcome(outcome);

        bet = betRepository.save(bet);
        wallet.setBalance(wallet.getBalance().subtract(sum));

        walletRepository.save(wallet);
        userPublisher.notifyAccountChange(user);

        return Result.success(bet);
    }

    public List<PlacedBetDto> getBets(User user, boolean showClosed) {
        List<Bet> result;

        if (showClosed) {
            result = betRepository.findByUser(user);
        } else {
            result = betRepository.findByUserAndOutcomeEventStatusIn(user,
                    List.of(EventStatus.PLANNED, EventStatus.ONGOING));
        }

        return result
                .stream()
                .map(PlacedBetDto::fromBet)
                .toList();
    }
}
