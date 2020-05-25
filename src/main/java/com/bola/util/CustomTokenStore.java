package com.bola.util;

import com.bola.repositories.AccessTokenRepository;
import com.bola.repositories.RefreshTokenRepository;
import com.bola.repositories.entities.AccessToken;
import com.bola.repositories.entities.RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomTokenStore implements TokenStore {

    private AccessTokenRepository accessTokenRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public CustomTokenStore(AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.readAuthentication(oAuth2AccessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return this.accessTokenRepository.findByTokenId(this.extractTokenKey(token))
                .map(AccessToken::getAuthentication)
                .orElse(null);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        String refreshToken = null;
        if (oAuth2AccessToken.getRefreshToken() != null) {
            refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
        }
        if (readAccessToken(oAuth2AccessToken.getValue()) != null) {
            this.removeAccessToken(oAuth2AccessToken);
        }
        AccessToken accessToken = new AccessToken();
        accessToken.setAuthentication(oAuth2Authentication);
        accessToken.setAuthenticationId(this.authenticationKeyGenerator.extractKey(oAuth2Authentication));
        accessToken.setClientId(oAuth2Authentication.getOAuth2Request().getClientId());
        accessToken.setRefreshToken(this.extractTokenKey(refreshToken));
        accessToken.setToken(oAuth2AccessToken);
        accessToken.setTokenId(this.extractTokenKey(oAuth2AccessToken.getValue()));
        accessToken.setUsername(oAuth2Authentication.isClientOnly() ? null : oAuth2Authentication.getName());
        this.accessTokenRepository.save(accessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String token) {
        return this.accessTokenRepository.findByTokenId(this.extractTokenKey(token))
                .map(AccessToken::getToken)
                .orElse(null);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        this.accessTokenRepository.findByTokenId(this.extractTokenKey(oAuth2AccessToken.getValue()))
                .ifPresent(accessToken -> this.accessTokenRepository.delete(accessToken));
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication oAuth2Authentication) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAuthentication(oAuth2Authentication);
        refreshToken.setRefreshToken(oAuth2RefreshToken);
        refreshToken.setTokenId(this.extractTokenKey(oAuth2RefreshToken.getValue()));
        this.refreshTokenRepository.save(refreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        return this.refreshTokenRepository.findByTokenId(this.extractTokenKey(token))
                .map(RefreshToken::getRefreshToken)
                .orElse(null);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        return this.refreshTokenRepository.findByTokenId(this.extractTokenKey(oAuth2RefreshToken.getValue()))
                .map(RefreshToken::getAuthentication)
                .orElse(null);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        this.refreshTokenRepository.findByTokenId(this.extractTokenKey(oAuth2RefreshToken.getValue()))
                .ifPresent(refreshToken -> this.refreshTokenRepository.delete(refreshToken));
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        this.accessTokenRepository.findByRefreshToken(this.extractTokenKey(oAuth2RefreshToken.getValue()))
                .ifPresent(accessToken -> this.accessTokenRepository.delete(accessToken));
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        OAuth2AccessToken oAuth2AccessToken = null;
        String authenticationId = this.authenticationKeyGenerator.extractKey(oAuth2Authentication);
        Optional<AccessToken> optionalAccessToken = this.accessTokenRepository.findByAuthenticationId(authenticationId);
        if (optionalAccessToken.isPresent()) {
            oAuth2AccessToken = optionalAccessToken.get().getToken();
            if (oAuth2AccessToken != null && !authenticationId.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(oAuth2AccessToken)))) {
                this.removeAccessToken(oAuth2AccessToken);
                this.storeAccessToken(oAuth2AccessToken, oAuth2Authentication);
            }
        }
        return oAuth2AccessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username) {
        return this.accessTokenRepository.findByClientIdAndUsername(clientId, username)
                .stream()
                .map(AccessToken::getToken)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return this.accessTokenRepository.findByClientId(clientId)
                .stream()
                .map(AccessToken::getToken)
                .collect(Collectors.toList());
    }

    private String extractTokenKey(String value) {
        if (value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }
}
