package ru.drobyazko.fooddeliveryservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        for (AuthorityType authorityType : registerUser.authorityTypeSet()) {
            AuthorityEntity authorityEntity = new AuthorityEntity(new AuthorityId(userEntity.getId(), authorityType.getAuthority()));
            authorityEntity = authorityRepository.save(authorityEntity);
        }
        return new User(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), registerUser.authorityTypeSet());
    }
}
