package com.bola.config;

import com.bola.repositories.AccessTokenRepository;
import com.bola.repositories.RefreshTokenRepository;
import com.bola.util.CustomPasswordEncrypt;
import com.bola.util.CustomTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    UserDetailsService customUserDetailsService;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;


    @Bean
    public TokenStore tokenStore() {
        return new CustomTokenStore(accessTokenRepository, refreshTokenRepository);
    }

    /**
     * Setting up the endpointsconfigurer authentication manager.
     * The AuthorizationServerEndpointsConfigurer defines the authorization and token endpoints and the token services.
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(this.tokenStore())
                .authenticationManager(this.authenticationManager)
//                .accessTokenConverter(this.defaultAccessTokenConverter())
                .userDetailsService(this.customUserDetailsService);
    }

    /**
     * Setting up the clients with a clientId, a clientSecret, a scope, the grant types and the authorities.
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("fooClientId").secret("secret")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("read", "write")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "USER", "ADMIN")
                .autoApprove(true)
                .accessTokenValiditySeconds(6000)//Access token is only valid for 3 minutes.
                .refreshTokenValiditySeconds(60000);
    }

    /**
     * We here defines the security constraints on the token endpoint.
     * We set it up to isAuthenticated, which returns true if the user is not anonymous
     *
     * @param security the AuthorizationServerSecurityConfigurer.
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

//    @Bean
//    public JwtAccessTokenConverter defaultAccessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("123");
//        return converter;
//    }

}
