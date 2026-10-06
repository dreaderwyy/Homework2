package org.example;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Account account = new Account(12345, 999, new BigDecimal("10000.00"), BankType.AUM);

        System.out.println("Добро пожаловать в банкомат!");

        System.out.print("Введите номер карты: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка доступа: номер карты должен быть целым числом.");
            return;
        }
        int cardNumber = scanner.nextInt();

        System.out.print("Введите пин-код: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка доступа: пин-код должен быть целым числом.");
            return;
        }
        int pinCode = scanner.nextInt();

        if (cardNumber != account.getCardNumber() || pinCode != account.getPinCode()) {
            System.out.println("Ошибка доступа: неверный номер карты или пин-код.");
            return;
        }

        System.out.println("Авторизация успешна.");
        System.out.println(account);

        CashMachine cashMachine = new CashMachine();

        System.out.print("Введите сумму для внесения (например, 500,50): ");
        BigDecimal depositAmount = readAmount(scanner);
        if (depositAmount == null) {
            System.out.println("Некорректная сумма. Баланс не изменен.");
        }
        account.balance = cashMachine.deposit(account.getBalance(), depositAmount);
        System.out.println("Баланс после внесения: " + account.getBalance() + " руб.");

        System.out.print("Введите сумму для снятия (например, 3000,00): ");
        BigDecimal withdrawAmount = readAmount(scanner);
        if (withdrawAmount == null) {
            System.out.println("Некорректная сумма.");
        }
        account.balance = cashMachine.withdraw(account.getBalance(), withdrawAmount, account.getBankType());
        System.out.println("Баланс после снятия: " + account.getBalance() + " руб.");

        System.out.println(account);
    }

    public static BigDecimal readAmount(Scanner scanner) {
        if (!scanner.hasNext()) {
            return null;
        }
        String text = scanner.next().replace(',', '.');

        int dotCount = 0;
        int digitCount = 0;
        int digitsAfterDot = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '.') {
                dotCount++;
                if (dotCount > 1) {
                    return null;
                }
            } else if (c >= '0' && c <= '9') {
                digitCount++;
                if (dotCount == 1) {
                    digitsAfterDot++;
                }
            } else {
                return null;
            }
        }

        if (digitCount == 0 || digitsAfterDot > 2) {
            return null;
        }
        return new BigDecimal(text);
    }
}
