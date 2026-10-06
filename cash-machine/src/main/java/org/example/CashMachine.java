package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashMachine implements WithdrawalOperations, DepositOperations {
    @Override
    public BigDecimal deposit(BigDecimal balance, BigDecimal amount) {
        if (balance == null) {
            System.out.println("Ошибка: баланс не задан.");
            return new BigDecimal("0.00");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return balance;
        }
        return balance.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal withdraw(BigDecimal balance, BigDecimal amount, BankType bankType) {
        if (balance == null) {
            System.out.println("Ошибка: баланс не задан.");
            return new BigDecimal("0.00");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Ошибка: некорректная сумма снятия.");
            return balance;
        }

        BigDecimal commission = applyCommission(amount, bankType);
        BigDecimal total = amount.add(commission);

        if (total.compareTo(balance) > 0) {
            System.out.println("Недостаточно средств на счете. Требуется: " + total + " руб.");
            return balance;
        }

        System.out.println("Комиссия банка: " + commission + " руб.");
        return balance.subtract(total).setScale(2, RoundingMode.HALF_UP);
    }
}
