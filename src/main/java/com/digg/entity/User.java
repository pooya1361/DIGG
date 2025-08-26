package com.digg.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Address is required")
    public String address;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "Telephone is required")
    public String telephone;

    public User() {}

    public User(String name, String address, String email, String telephone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
    }

    // Custom finder methods
    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static List<User> findByName(String name) {
        return find("name LIKE ?1", "%" + name + "%").list();
    }
}