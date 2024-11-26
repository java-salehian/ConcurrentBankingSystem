package azki.vam.banking;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.service.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class BankingApplication
        implements CommandLineRunner
{
    private final BankService bankService;

    public BankingApplication(BankService bankService) {
        this.bankService = bankService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Balance\n6. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter account holder name:");
                    String name = scanner.next();
                    System.out.println("Enter initial balance:");
                    double balance = scanner.nextDouble();
                    BankAccount account = bankService.createAccount(name, balance);
                    System.out.println("Account created successfully with accountNumber: " + account.getAccountNumber());
                }
                case 2 -> {
                    System.out.println("Enter account number:");
                    String depositAccountNumber = scanner.next();
                    System.out.println("Enter amount to deposit:");
                    double depositAmount = scanner.nextDouble();
                    boolean success = bankService.deposit(depositAccountNumber, depositAmount);
                    if (success) {
                        System.out.println("Deposit successful");
                    } else {
                        System.out.println("Deposit not successful");
                    }
                }
                case 3 -> {
                    System.out.println("Enter account number:");
                    String withdrawAccountNumber = scanner.next();
                    System.out.println("Enter amount to withdraw:");
                    double withdrawAmount = scanner.nextDouble();
                    boolean success = bankService.withdraw(withdrawAccountNumber, withdrawAmount);
                    if (success) {
                        System.out.println("Withdraw successful");
                    } else {
                        System.out.println("Withdraw not successful");
                    }
                }
                case 4 -> {
                    System.out.println("Enter from account number:");
                    String fromAccountNumber = scanner.next();
                    System.out.println("Enter to account number:");
                    String toAccountNumber = scanner.next();
                    System.out.println("Enter amount to transfer:");
                    double transferAmount = scanner.nextDouble();
                    boolean success = bankService.transfer(fromAccountNumber, toAccountNumber, transferAmount);
                    if (success) {
                        System.out.println("Transfer successful");
                    } else {
                        System.out.println("Transfer not successful");
                    }
                }
                case 5 -> {
                    System.out.println("Enter account number:");
                    String accountAccountNumber = scanner.next();
                    double accountBalance = bankService.getBalance(accountAccountNumber);
                    System.out.println("Account Balance: " + accountBalance);
                }
                case 6 -> {
                    scanner.close();
                    System.exit(0);
                }
            }
        }
    }
}

