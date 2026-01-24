package ru.drobyazko.fooddeliveryservice;

public enum TestUsers {
    USER("user", "user"),
    KITCHEN("kitchen", "kitchen"),
    ADMIN("admin", "admin");

    private final String username;
    private final String password;

    TestUsers(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
