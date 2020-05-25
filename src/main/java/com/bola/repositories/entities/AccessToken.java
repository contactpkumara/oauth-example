package com.bola.repositories.entities;

import com.bola.util.SerializableObjectConverter;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.*;

@Entity
@Table(name = "access_token")
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 20)
    private Integer id;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "token")
    private String token;
    @Column(name = "authentication_id")
    private String authenticationId;
    @Column(name = "username")
    private String username;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "authentication")
    private String authentication;
    @Column(name = "refresh_token")
    private String refreshToken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public OAuth2AccessToken getToken() {
        byte[] bytes = Base64.decodeBase64(token);
        return (OAuth2AccessToken) SerializationUtils.deserialize(bytes);
    }

    public void setToken(OAuth2AccessToken token) {
        byte[] bytes = SerializationUtils.serialize(token);
        this.token = Base64.encodeBase64String(bytes);
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public OAuth2Authentication getAuthentication() {
        return (OAuth2Authentication) SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
