package azki.vam.banking.service.transaction;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.enummeration.TransactionType;
import org.springframework.stereotype.Service;

/*
 *  Soha Salehian created on 11/21/2024
 */
@Service
public class DepositStrategy implements TransactionStrategy {
    @Override
    public boolean execute(BankAccount fromAccount, BankAccount toAccount, double amount) {
        fromAccount.deposit(amount);
        return true;
    }
}


