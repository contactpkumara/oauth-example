package com.bola.repositories.entities;

import com.bola.util.SerializableObjectConverter;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.*;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 20)
    private Integer id;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "token")
    private String refreshToken;
    @Column(name = "authentication")
    private String authentication;

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

    public OAuth2RefreshToken getRefreshToken() {
        byte[] bytes = Base64.decodeBase64(refreshToken);
        return (OAuth2RefreshToken) SerializationUtils.deserialize(bytes);
    }

    public void setRefreshToken(OAuth2RefreshToken refreshToken) {
        byte[] bytes = SerializationUtils.serialize(refreshToken);
        this.refreshToken = Base64.encodeBase64String(bytes);
    }

    public OAuth2Authentication getAuthentication() {
        return SerializableObjectConverter.deserialize(authentication);
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = SerializableObjectConverter.serialize(authentication);
    }
}
