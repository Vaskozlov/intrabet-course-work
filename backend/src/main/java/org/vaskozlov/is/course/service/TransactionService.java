package org.vaskozlov.is.course.service;

import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Transaction;
import org.vaskozlov.is.course.bean.TransactionType;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.lib.Result;

@Service
public class TransactionService {
    public Result<Transaction, String> makeTransaction(User user, TransactionType type, Long amount) {
        return null;
    }
}
