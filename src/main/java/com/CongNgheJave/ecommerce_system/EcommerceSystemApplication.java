package com.CongNgheJave.ecommerce_system;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class EcommerceSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner createAdminUser(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminUsername = "hari08@gmail.com";
			if (userRepository.findByUsername(adminUsername).isEmpty()) {
				AppUser admin = new AppUser();
				admin.setUsername(adminUsername);
				admin.setEmail(adminUsername);
				admin.setFullName("Admin Hari");
				admin.setRole("ROLE_ADMIN");
				admin.setPassword(passwordEncoder.encode("HaiCon2508@"));
				userRepository.save(admin);
				log.info("Admin account created successfully! Username/Email: {}", adminUsername);
			}

			// Diagnostic logging of all users in DB
			log.info("========== [DB USERS AUDIT START] ==========");
			try {
				userRepository.findAll().forEach(user -> {
					log.info("User loaded: ID={}, Username='{}', Role='{}'", user.getId(), user.getUsername(), user.getRole());
				});
			} catch (Exception e) {
				log.error("Error reading users: ", e);
			}
			log.info("========== [DB USERS AUDIT END] ==========");

		};
	}
}
