package ru.drobyazko.fooddeliveryapp;

import org.springframework.boot.SpringApplication;

public class TestFoodDeliveryAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(FoodDeliveryAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
