package azki.vam.banking;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.repository.BankAccountRepository;
import azki.vam.banking.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BankingApplicationTests {

    Logger logger = LoggerFactory.getLogger(BankingApplicationTests.class);

    @Autowired
    private BankService bankService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private BankAccount account1;
    private BankAccount account2;

    @BeforeEach
    @Transactional
    public void setUp() {
        account1 = bankService.createAccount("User1", 1000.0);
        account2 = bankService.createAccount("User2", 1000.0);
    }

    @Test
    public void testConcurrentDeposits() throws InterruptedException {
        double startBalance = account1.getBalance();
        int numberOfThreads = 10;
        double depositAmount = 100.0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        logger.info("numberOfThreads: " + numberOfThreads + ", depositAmount: " + depositAmount);
        logger.info("startBalance: " + startBalance);

        AtomicInteger successCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                if (bankService.deposit(account1.getAccountNumber(), depositAmount)) {
                    successCount.getAndIncrement();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        BankAccount updatedAccount = bankAccountRepository.findById(account1.getId()).orElseThrow();

        logger.info("successCount: " + successCount + ", updatedAccount: " + updatedAccount);

        assertEquals(startBalance + successCount.get() * depositAmount, updatedAccount.getBalance());
    }

    @Test
    public void testConcurrentWithdrawals() throws InterruptedException {
        double startBalance = account1.getBalance();
        int numberOfThreads = 10;
        double withdrawalAmount = 50.0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        logger.info("numberOfThreads: " + numberOfThreads + ", withdrawalAmount: " + withdrawalAmount);
        logger.info("startBalance: " + startBalance);

        AtomicInteger successCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                if (bankService.withdraw(account1.getAccountNumber(), withdrawalAmount)) {
                    successCount.getAndIncrement();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        BankAccount updatedAccount = bankAccountRepository.findById(account1.getId()).orElseThrow();

        logger.info("successCount: " + successCount + ", updatedAccount: " + updatedAccount);

        assertEquals(startBalance - successCount.get() * withdrawalAmount, updatedAccount.getBalance());
    }

    @Test
    public void testConcurrentTransfers() throws InterruptedException {
        double startBalance1 = account1.getBalance();
        double startBalance2 = account2.getBalance();
        int numberOfThreads = 10;
        double transferAmount = 20.0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        logger.info("numberOfThreads: " + numberOfThreads + ", transferAmount: " + transferAmount);
        logger.info("startBalance1: " + startBalance1 + ", startBalance2: " + startBalance2);

        AtomicInteger successCount = new AtomicInteger();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                if (bankService.transfer(account1.getAccountNumber(), account2.getAccountNumber(), transferAmount)) {
                    successCount.getAndIncrement();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        BankAccount updatedAccount1 = bankAccountRepository.findById(account1.getId()).orElseThrow();
        BankAccount updatedAccount2 = bankAccountRepository.findById(account2.getId()).orElseThrow();
        double expectedBalanceAccount1 = startBalance1 - successCount.get() * transferAmount;
        double expectedBalanceAccount2 = startBalance2 + successCount.get() * transferAmount;

        logger.info("successCount: " + successCount + ", updatedAccount1: " + updatedAccount1 + ", updatedAccount2: " + updatedAccount2);

        assertEquals(expectedBalanceAccount1, updatedAccount1.getBalance());
        assertEquals(expectedBalanceAccount2, updatedAccount2.getBalance());
    }

    @Test
    public void testConcurrentTransactions() throws InterruptedException {
        double startBalance1 = account1.getBalance();
        double startBalance2 = account2.getBalance();
        int numberOfThreads = 20;
        double transactionAmount = 20.0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        logger.info("Starting concurrent transactions test.");
        logger.info("Number of Threads: {}, Transaction Amount: {}", numberOfThreads, transactionAmount);
        logger.info("Start Balance - Account1: {}, Account2: {}", startBalance1, startBalance2);
        AtomicInteger successCountOp1 = new AtomicInteger(); // deposit 1
        AtomicInteger successCountOp2 = new AtomicInteger(); // deposit 2
        AtomicInteger successCountOp3 = new AtomicInteger(); // withdraw 1
        AtomicInteger successCountOp4 = new AtomicInteger(); // withdraw 2
        AtomicInteger successCountOp5 = new AtomicInteger(); // transfer 1 to 2
        AtomicInteger successCountOp6 = new AtomicInteger(); // transfer 2 to 1
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                Random random = new Random();
                int opCase = random.nextInt(6) + 1; // Generates number between 1 and 6
                switch (opCase) {
                    case 1:
                        if (bankService.deposit(account1.getAccountNumber(), transactionAmount)) {
                            successCountOp1.incrementAndGet();
                        }
                        break;
                    case 2:
                        if (bankService.deposit(account2.getAccountNumber(), transactionAmount)) {
                            successCountOp2.incrementAndGet();
                        }
                        break;
                    case 3:
                        if (bankService.withdraw(account1.getAccountNumber(), transactionAmount)) {
                            successCountOp3.incrementAndGet();
                        }
                        break;
                    case 4:
                        if (bankService.withdraw(account2.getAccountNumber(), transactionAmount)) {
                            successCountOp4.incrementAndGet();
                        }
                        break;
                    case 5:
                        if (bankService.transfer(account1.getAccountNumber(), account2.getAccountNumber(), transactionAmount)) {
                            successCountOp5.incrementAndGet();
                        }
                        break;
                    case 6:
                        if (bankService.transfer(account2.getAccountNumber(), account1.getAccountNumber(), transactionAmount)) {
                            successCountOp6.incrementAndGet();
                        }
                        break;
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        BankAccount updatedAccount1 = bankAccountRepository.findById(account1.getId()).orElseThrow();
        BankAccount updatedAccount2 = bankAccountRepository.findById(account2.getId()).orElseThrow();
        double expectedBalanceAccount1 = startBalance1 + successCountOp1.get() * transactionAmount - successCountOp3.get() * transactionAmount - successCountOp5.get() * transactionAmount + successCountOp6.get() * transactionAmount;
        double expectedBalanceAccount2 = startBalance2 + successCountOp2.get() * transactionAmount - successCountOp4.get() * transactionAmount + successCountOp5.get() * transactionAmount - successCountOp6.get() * transactionAmount;
        logger.info("Success Count - Deposit1: {}, Deposit2: {}, Withdraw1: {}, Withdraw2: {}, Transfer1to2: {}, Transfer2to1: {}", successCountOp1, successCountOp2, successCountOp3, successCountOp4, successCountOp5, successCountOp6);
        logger.info("Updated Balance - Account1: {}, Account2: {}", updatedAccount1.getBalance(), updatedAccount2.getBalance());
        assertEquals(expectedBalanceAccount1, updatedAccount1.getBalance());
        assertEquals(expectedBalanceAccount2, updatedAccount2.getBalance());
    }


}