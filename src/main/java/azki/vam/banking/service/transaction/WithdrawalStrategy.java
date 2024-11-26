package azki.vam.banking.service.transaction;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.enummeration.TransactionType;
import azki.vam.banking.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalStrategy implements TransactionStrategy {
    @Override
    public boolean execute(BankAccount fromAccount, BankAccount toAccount, double amount) {
        if (!fromAccount.withdraw(amount)) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + fromAccount.getAccountNumber());
        } else {
            return true;
        }
    }
}
