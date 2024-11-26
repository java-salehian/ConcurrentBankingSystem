package azki.vam.banking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class BankingApplication
        implements CommandLineRunner
{
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Balance\n6. Exit");
            int choice = scanner.nextInt();
        }
    }
}

