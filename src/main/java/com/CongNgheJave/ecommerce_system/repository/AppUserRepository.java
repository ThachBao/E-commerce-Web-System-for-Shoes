package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM AppUser u WHERE " +
           "(:role IS NULL OR :role = '' OR u.role = :role) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AppUser> searchUsers(@Param("keyword") String keyword, @Param("role") String role, Pageable pageable);
}
