package ru.drobyazko.fooddeliveryservice.security.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, AuthorityId> {
    Set<AuthorityEntity> findAuthorityEntityByAuthorityId_UserId(Long userId);
}
