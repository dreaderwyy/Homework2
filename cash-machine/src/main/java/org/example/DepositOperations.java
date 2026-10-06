package org.example;

import java.math.BigDecimal;

public interface DepositOperations {
    BigDecimal deposit(BigDecimal balance, BigDecimal amount);
}
