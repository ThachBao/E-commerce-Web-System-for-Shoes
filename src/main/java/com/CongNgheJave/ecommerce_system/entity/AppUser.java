package com.CongNgheJave.ecommerce_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "passwordHash")
    private String password;

    @Column(name = "role")
    private String role = "ROLE_USER";

    @Column(name = "email")
    private String email;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Column(name = "isSystem")
    private Boolean isSystem = false;

    @Column(name = "forceChangePassword")
    private Boolean forceChangePassword = false;

    public AppUser() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Dùng để hiển thị username nếu cần.
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Dùng cho thông tin customer.
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Dùng để hiển thị tên khách hàng trong trang admin order.
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Dùng để hiển thị số điện thoại customer.
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getForceChangePassword() {
        return forceChangePassword;
    }

    public void setForceChangePassword(Boolean forceChangePassword) {
        this.forceChangePassword = forceChangePassword;
    }
}