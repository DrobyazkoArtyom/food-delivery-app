package ru.drobyazko.fooddeliveryservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> UsernameNotFoundException.fromUsername(username));
        Set<AuthorityEntity> authorityEntitySet = authorityRepository.findAuthorityEntityByAuthorityId_UserId(userEntity.getId());
        Set<Authority> authoritySet = authorityEntitySet.stream()
                .map(authorityEntity -> Authority.valueOf(authorityEntity.getAuthorityId().getAuthority()))
                .collect(Collectors.toSet());
        return new User(username, userEntity.getPassword(), authoritySet);
    }
}
