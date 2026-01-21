package ru.drobyazko.fooddeliveryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import ru.drobyazko.fooddeliveryservice.security.Authority;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/users/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/kitchens/**").permitAll()
                                .requestMatchers("/kitchens/**").hasAuthority(Authority.KITCHEN.getAuthority())
                                .requestMatchers(HttpMethod.GET, "/menuItems/**").permitAll()
                                .requestMatchers("/menuItems/**").hasAuthority(Authority.KITCHEN.getAuthority())
                                .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                                .requestMatchers("/orders/**").hasAuthority(Authority.USER.getAuthority())
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
