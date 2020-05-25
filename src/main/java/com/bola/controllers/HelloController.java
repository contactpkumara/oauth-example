package com.bola.controllers;

import com.bola.repositories.AccessTokenRepository;
import com.bola.repositories.RefreshTokenRepository;
import com.bola.repositories.UserRepository;
import com.bola.repositories.entities.Adviser;
import com.bola.util.CustomTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Adviser> findByUsername(@PathVariable("username") String username) {
        return userRepository.findByUsernameAndActiveStatus(username, true)
                .map(adviser -> new ResponseEntity<>(adviser, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("authorization") String authorization) {
        CustomTokenStore customTokenStore = new CustomTokenStore(accessTokenRepository, refreshTokenRepository);
        try {
            String token = authorization.substring(7);
            OAuth2AccessToken oAuth2AccessToken = customTokenStore.readAccessToken(token);
            OAuth2RefreshToken oAuth2RefreshToken = customTokenStore.readRefreshToken(oAuth2AccessToken.getValue());
            customTokenStore.removeAccessToken(oAuth2AccessToken);
            customTokenStore.removeRefreshToken(oAuth2AccessToken.getRefreshToken());
            return new ResponseEntity<>("Logout Success", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Unable to logout", HttpStatus.OK);
        }
    }
}
