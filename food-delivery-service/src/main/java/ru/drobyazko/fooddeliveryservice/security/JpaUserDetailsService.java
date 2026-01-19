package ru.drobyazko.fooddeliveryservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //TODO: add actual authorities
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> UsernameNotFoundException.fromUsername(username));
        return new User(username, userEntity.getPassword(), List.of(new SimpleGrantedAuthority("ADMIN")));
    }
}
