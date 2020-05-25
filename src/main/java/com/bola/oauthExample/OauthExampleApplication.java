package com.bola.oauthExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.bola")
@EnableJpaRepositories("com.bola.repositories")
@EntityScan("com.bola.repositories.entities")
public class OauthExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthExampleApplication.class, args);
	}

}
