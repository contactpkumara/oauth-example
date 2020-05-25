package com.bola.repositories;

import com.bola.repositories.entities.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    List<AccessToken> findByClientId(String clientId);

    List<AccessToken> findByClientIdAndUsername(String clientId, String username);

    Optional<AccessToken> findByTokenId(String tokenId);

    Optional<AccessToken> findByRefreshToken(String refreshToken);

    Optional<AccessToken> findByAuthenticationId(String authenticationId);
}
