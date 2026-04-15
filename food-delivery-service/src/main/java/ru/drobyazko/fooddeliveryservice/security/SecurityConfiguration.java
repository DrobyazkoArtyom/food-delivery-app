package ru.drobyazko.fooddeliveryservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.Authority;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/**").permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers("/users/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/kitchens/**").permitAll()
                                .requestMatchers("/kitchens/**").hasAuthority(Authority.KITCHEN.getAuthority())
                                .requestMatchers(HttpMethod.GET, "/menuItems/**").permitAll()
                                .requestMatchers("/menuItems/**").hasAuthority(Authority.KITCHEN.getAuthority())
                                .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                                .requestMatchers("/orders/**").hasAuthority(Authority.USER.getAuthority())
                                .requestMatchers("/ws").hasAuthority(Authority.USER.getAuthority())
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
