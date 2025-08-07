package com.easypg.entities;

import java.time.LocalDate;

import com.easypg.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(length= 30,nullable = false)
    private String username;

	@Column(length = 30,nullable = false)
    private String password;

    
    @Column(unique = true,length=30,nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="user_role",nullable = false)
    private UserRole userRole;
}
