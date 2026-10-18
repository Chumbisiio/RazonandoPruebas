package com.grupoj.Razonando_Back.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DocumentTypes documentType;

    @Column(name = "id_number", nullable = false, unique = true,  length = 15)
    @Size(max = 15)
    private String idNumber;

    @Column(name = "names", nullable = false, length = 80)
    @Size(max = 80)
    private String names;

    @Column(name = "surnames", nullable = false, length = 80)
    @Size(max = 80)
    private String surNames;

    @Column(name = "nickname", nullable = true, unique = true, length = 50)
    @Size(max = 50)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 80)
    @Size(max = 80)
    private String email;

    @Column(name = "state", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStates state;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UsersRoles> usersRoles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "photo_url", nullable = true, length = 255)
    @Size(max = 255)
    private String photoUrl;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Users that = (Users) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

