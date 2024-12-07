package azki.vam.banking.service;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.exception.AccountNotFoundException;
import azki.vam.banking.log.TransactionObserver;
import azki.vam.banking.repository.BankAccountRepository;
import azki.vam.banking.service.transaction.TransactionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankAccountRepository bankAccountRepository;
    private final List<TransactionObserver> observers;
    private final TransactionContext transactionContext;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BankAccount createAccount(String accountHolderName, double initialBalance) {
        BankAccount account = new BankAccount(generateAccountNumber(), accountHolderName, initialBalance);
        bankAccountRepository.save(account);
        return account;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean deposit(String accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number not found" + accountNumber));

        boolean success = transactionContext.executeStrategy("depositStrategy", account, null, amount);

        notifyObservers(account.getAccountNumber(), "Deposit", amount);
        return success;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean withdraw(String accountNumber, double amount) {
        BankAccount account = bankAccountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number not found" + accountNumber));

        boolean success = transactionContext.executeStrategy("withdrawalStrategy", account, null, amount);

        if (success) {
            notifyObservers(account.getAccountNumber(), "Withdrawal", amount);
        }
        return success;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        BankAccount fromAccount = bankAccountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number not found" + fromAccountNumber));
        BankAccount toAccount = bankAccountRepository.findByAccountNumberForUpdate(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number not found" + toAccountNumber));


        boolean success = transactionContext.executeStrategy("transferStrategy", fromAccount, toAccount, amount);
        if (success) {
            notifyObservers(fromAccount.getAccountNumber(), "Transfer-From", amount);
            notifyObservers(toAccount.getAccountNumber(), "Transfer-To", amount);
        }
        return success;
    }

    @Transactional(readOnly = true)
    public double getBalance(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account Number not found" + accountNumber));
        return account.getBalance();
    }

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    private void notifyObservers(String accountNumber, String transactionType, double amount) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(accountNumber, transactionType, amount);
        }
    }
}
