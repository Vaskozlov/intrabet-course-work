package org.vaskozlov.is.course.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Bet;
import org.vaskozlov.is.course.bean.EventStatus;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.dto.BetDTO;
import org.vaskozlov.is.course.dto.PlacedBetDto;
import org.vaskozlov.is.course.lib.Result;
import org.vaskozlov.is.course.repository.BetRepository;
import org.vaskozlov.is.course.repository.OutcomeRepository;
import org.vaskozlov.is.course.repository.WalletRepository;
import org.vaskozlov.is.course.service.notifications.UserNotificationService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BetService {
    private final OutcomeRepository outcomeRepository;
    private final BetRepository betRepository;
    private final WalletRepository walletRepository;
    private final UserNotificationService userNotificationService;

    @Autowired
    public BetService(OutcomeRepository outcomeRepository, BetRepository betRepository, WalletRepository walletRepository, UserNotificationService userNotificationService) {
        this.outcomeRepository = outcomeRepository;
        this.betRepository = betRepository;
        this.walletRepository = walletRepository;
        this.userNotificationService = userNotificationService;
    }

    @Transactional
    public Result<Bet, String> place(BetDTO betDTO, User user) {
        var wallet = user.getWallet();
        var sum = BigDecimal.valueOf(betDTO.getSum(), 2);

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
        userNotificationService.notifyAccountChange(user);

        return Result.success(bet);
    }

    public List<PlacedBetDto> getBets(User user, boolean showClosed) {
        List<Bet> result;

        if (showClosed) {
            result = betRepository.findByUser(user);
        } else {
            result = betRepository.findByUserAndOutcomeEventStatusIn(user, List.of(EventStatus.PLANNED, EventStatus.ONGOING));
        }

        return result
                .stream()
                .map(PlacedBetDto::fromBet)
                .toList();
    }
}
