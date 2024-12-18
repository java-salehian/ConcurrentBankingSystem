package azki.vam.banking.service.transaction;

import azki.vam.banking.entity.BankAccount;

/*
 *  Soha Salehian created on 11/21/2024
 */
public interface TransactionStrategy {
    boolean execute(BankAccount fromAccount, BankAccount toAccount, double amount);
}

