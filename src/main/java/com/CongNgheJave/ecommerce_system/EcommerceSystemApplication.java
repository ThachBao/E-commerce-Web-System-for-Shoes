package com.CongNgheJave.ecommerce_system;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import com.CongNgheJave.ecommerce_system.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
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
				System.out.println("Admin account created successfully! Username/Email: " + adminUsername);
			}

			// Diagnostic logging of all users in DB
			System.out.println("========== [DB USERS AUDIT START] ==========");
			try {
				userRepository.findAll().forEach(user -> {
					System.out.println("User loaded: ID=" + user.getId() + ", Username='" + user.getUsername() + "', Role='" + user.getRole() + "'");
				});
			} catch (Exception e) {
				System.out.println("Error reading users: " + e.getMessage());
			}
			System.out.println("========== [DB USERS AUDIT END] ==========");

			// Auto-fix and reset passwords/roles for seeded SQL users to ensure they are guaranteed to match 'admin123'
			userRepository.findByUsername("rootadmin").ifPresent(user -> {
				user.setRole("ROLE_ADMIN");
				user.setPassword(passwordEncoder.encode("admin123"));
				userRepository.save(user);
				System.out.println("========== [AUTO-FIX FORCE] rootadmin -> ROLE_ADMIN / admin123");
			});
			userRepository.findByUsername("admin01").ifPresent(user -> {
				user.setRole("ROLE_ADMIN");
				user.setPassword(passwordEncoder.encode("admin123"));
				userRepository.save(user);
				System.out.println("========== [AUTO-FIX FORCE] admin01 -> ROLE_ADMIN / admin123");
			});
			userRepository.findByUsername("staff01").ifPresent(user -> {
				user.setRole("ROLE_STAFF");
				user.setPassword(passwordEncoder.encode("admin123"));
				userRepository.save(user);
				System.out.println("========== [AUTO-FIX FORCE] staff01 -> ROLE_STAFF / admin123");
			});
			userRepository.findByUsername("staff02").ifPresent(user -> {
				user.setRole("ROLE_STAFF");
				user.setPassword(passwordEncoder.encode("admin123"));
				userRepository.save(user);
				System.out.println("========== [AUTO-FIX FORCE] staff02 -> ROLE_STAFF / admin123");
			});
		};
	}
}
