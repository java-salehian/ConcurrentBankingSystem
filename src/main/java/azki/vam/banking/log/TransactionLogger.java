package azki.vam.banking.log;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 *  Soha Salehian created on 11/21/2024
 */
@Component
public class TransactionLogger implements TransactionObserver {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Object lock = new Object();

    @Override
    public void onTransaction(String accountNumber, String transactionType, double amount) {
        executorService.submit(() -> logTransaction(accountNumber, transactionType, amount));
    }

    private void logTransaction(String accountNumber, String transactionType, double amount) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter("transactions.log", true)) {
                writer.write(String.format("Account: %s | Transaction: %s | Amount: %.2f%n", accountNumber, transactionType, amount));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}