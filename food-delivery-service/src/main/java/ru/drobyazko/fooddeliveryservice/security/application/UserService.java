package ru.drobyazko.fooddeliveryservice.security.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.Authority;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.RegisterUser;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.User;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.AuthorityEntity;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.AuthorityId;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.AuthorityRepository;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserAlreadyExistsException;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserEntity;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       AuthorityRepository authorityRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterUser registerUser) {
        if (userRepository.existsByUsername(registerUser.username())) {
            throw new UserAlreadyExistsException();
        }
        UserEntity userEntity = new UserEntity(registerUser.username(), passwordEncoder.encode(registerUser.password()), true);
        userEntity = userRepository.save(userEntity);
        for (Authority authority : registerUser.authoritySet()) {
            AuthorityEntity authorityEntity = new AuthorityEntity(new AuthorityId(userEntity.getId(), authority.getAuthority()));
            authorityRepository.save(authorityEntity);
        }
        return new User(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), registerUser.authoritySet());
    }
}
