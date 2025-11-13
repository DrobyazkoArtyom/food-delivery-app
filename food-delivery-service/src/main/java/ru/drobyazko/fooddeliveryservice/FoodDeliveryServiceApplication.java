package ru.drobyazko.fooddeliveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//TODO: need to delete this exclude when i get to spring security
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class FoodDeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryServiceApplication.class, args);
    }

}
