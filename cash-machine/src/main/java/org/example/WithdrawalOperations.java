package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface WithdrawalOperations {
    BigDecimal withdraw(BigDecimal balance, BigDecimal amount, BankType bankType);

    default BigDecimal applyCommission(BigDecimal amount, BankType bankType) {
        if (amount == null || bankType == null) {
            return new BigDecimal("0.00");
        }
        return amount.multiply(bankType.getCommission()).setScale(2, RoundingMode.HALF_UP);
    }
}
