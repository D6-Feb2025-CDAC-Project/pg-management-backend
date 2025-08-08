package com.easypg;

import java.util.Optional;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication // includes @Configuration
public class Application {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
        String mailPassword = dotenv.get("EASYPG_MAIL_PASSWORD");
//        System.out.println("Loaded mail password: " + mailPassword);
		
		SpringApplication.run(Application.class, args);
	}

	@Bean 
	public ModelMapper modelMapper() {
		System.out.println("in model mapper creation");
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				.setMatchingStrategy(MatchingStrategies.STRICT)
				.setPropertyCondition(Conditions.isNotNull());// use case - PUT
		return mapper;

	}
	
	@Bean
	public AuditorAware<String> auditorProvider() {
	    return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

}
