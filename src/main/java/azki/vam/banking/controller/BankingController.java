package azki.vam.banking.controller;

import azki.vam.banking.entity.BankAccount;
import azki.vam.banking.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 *  Soha Salehian created on 11/21/2024
 */
@RestController
@RequestMapping("/api/banking")
public class BankingController {
    private final BankService bankService;

    public BankingController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam String accountHolderName, @RequestParam double initialBalance) {
        BankAccount account = bankService.createAccount(accountHolderName, initialBalance);
        return ResponseEntity.ok("Account created successfully with accountNumber: " + account.getAccountNumber());
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam String accountNumber, @RequestParam double amount) {
        boolean success = bankService.deposit(accountNumber, amount);
        if (success) {
            return ResponseEntity.ok("Deposit successful");
        } else {
            return ResponseEntity.badRequest().body("Deposit not successful");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam String accountNumber, @RequestParam double amount) {
        boolean success = bankService.withdraw(accountNumber, amount);
        if (success) {
            return ResponseEntity.ok("Withdrawal successful");
        } else {
            return ResponseEntity.badRequest().body("Withdrawal not successful");

        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam double amount) {
        boolean success = bankService.transfer(fromAccountNumber, toAccountNumber, amount);
        if (success) {
            return ResponseEntity.ok("Transfer successful");
        } else {
            return ResponseEntity.badRequest().body("Transfer not successful");
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<String> balance(@RequestParam String accountNumber) {
        double balance = bankService.getBalance(accountNumber);
        return ResponseEntity.ok("Account Balance: " + balance);
    }
}


