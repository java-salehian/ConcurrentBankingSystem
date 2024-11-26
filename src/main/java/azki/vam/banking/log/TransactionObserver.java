package azki.vam.banking.log;

/*
 *  Soha Salehian created on 11/21/2024
 */
public interface TransactionObserver {
    void onTransaction(String accountNumber, String transactionType, double amount);
}
