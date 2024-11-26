package azki.vam.banking.repository;

import azki.vam.banking.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 *  Soha Salehian created on 11/21/2024
 */
@Transactional
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.accountNumber = :accountNumber")
    Optional<BankAccount> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}

