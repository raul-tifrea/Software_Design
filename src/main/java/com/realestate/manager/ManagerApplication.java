package com.realestate.manager;

import com.realestate.manager.model.entity.User;
import com.realestate.manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;





@SpringBootApplication
public class ManagerApplication {

    public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}

}
