package azki.vam.banking.exception;

/*
 *  Soha Salehian created on 11/24/2024
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}

