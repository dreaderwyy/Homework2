package org.example;

public class User {
    @NotNull(message = "Имя обязательно")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String name;

    @NotNull(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Range(min = 0, max = 150, message = "Возраст должен быть от 0 до 150")
    private Integer age;

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}
