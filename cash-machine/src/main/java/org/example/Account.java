package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account {
    public int cardNumber;
    public int pinCode;
    public BigDecimal balance;
    public BankType bankType;

    public Account(int cardNumber, int pinCode, BigDecimal balance, BankType bankType) {
        if (cardNumber >= 10000 && cardNumber <= 99999) {
            this.cardNumber = cardNumber;
        } else {
            System.out.println("Ошибка: номер карты должен состоять из 5 цифр. Установлено значение 0.");
            this.cardNumber = 0;
        }

        if (pinCode >= 100 && pinCode <= 999) {
            this.pinCode = pinCode;
        } else {
            System.out.println("Ошибка: пин-код должен состоять из 3 цифр. Установлено значение 0.");
            this.pinCode = 0;
        }

        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Ошибка: некорректный баланс. Установлено значение 0.00.");
            this.balance = new BigDecimal("0.00");
        } else {
            this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        }

        if (bankType == null) {
            this.bankType = BankType.NEO;
        } else {
            this.bankType = bankType;
        }
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getPinCode() {
        return pinCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankType getBankType() {
        return bankType;
    }

    @Override
    public String toString() {
        return bankType.getRussianName() + " Карта: " + cardNumber + ", Баланс: " + balance + " руб.";
    }
}
