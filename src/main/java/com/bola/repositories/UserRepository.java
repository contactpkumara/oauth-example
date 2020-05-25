package com.bola.repositories;

import com.bola.repositories.entities.Adviser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<Adviser, Integer> {
    Optional<Adviser> findByUsernameAndActiveStatus(String username, Boolean activeStatus);
}
