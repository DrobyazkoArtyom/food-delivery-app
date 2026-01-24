package ru.drobyazko.fooddeliveryservice.security.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.RegisterUser;
import ru.drobyazko.fooddeliveryservice.security.domain.aggregate.User;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public User registerUser(RegisterUser registerUser) {
        UserEntity userEntity = new UserEntity(registerUser.username(), registerUser.password(), true);
        userEntity = userRepository.save(userEntity);
        for (Authority authority : registerUser.authoritySet()) {
            AuthorityEntity authorityEntity = new AuthorityEntity(new AuthorityId(userEntity.getId(), authority.getAuthority()));
            authorityEntity = authorityRepository.save(authorityEntity);
        }
        return new User(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), registerUser.authoritySet());
    }
}
