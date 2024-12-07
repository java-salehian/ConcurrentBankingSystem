package azki.vam.banking.service.transaction;

import azki.vam.banking.entity.BankAccount;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionContext {
    private final Map<String, TransactionStrategy> strategyMap;
//    private TransactionStrategy strategy;

    public TransactionContext(Map<String, TransactionStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

//    public void setStrategy(String strategyName) {
//        this.strategy = strategyMap.get(strategyName);
//    }

    public boolean executeStrategy(String strategyName, BankAccount fromAccount, BankAccount toAccount, double amount) {
        TransactionStrategy strategy = strategyMap.get(strategyName);
        if (strategy != null) {
            return strategy.execute(fromAccount, toAccount, amount);
        } else {
            throw new IllegalArgumentException("Strategy not found");
        }
    }
}

