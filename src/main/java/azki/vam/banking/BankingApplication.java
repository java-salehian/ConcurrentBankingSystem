package azki.vam.banking;

import azki.vam.banking.service.BankService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingApplication {
    private final BankService bankService;

    public BankingApplication(BankService bankService) {
        this.bankService = bankService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

}