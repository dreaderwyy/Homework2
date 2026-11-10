package org.example;

public class Main {
    public static void main(String[] args) {
        User user = new User("A", "invalid-email", 20);
        ValidationResult result = Validator.validate(user);

        if (!result.isValid()) {
            for (ValidationError error : result.getErrors()) {
                System.out.println(error.fieldName() + ": " + error.message());
            }
        }

        User correct = new User("Анна", "anna@example.com", 19);
        System.out.println("Корректный пользователь валиден: " + Validator.validate(correct).isValid());
    }
}
