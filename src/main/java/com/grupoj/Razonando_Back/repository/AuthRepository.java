package com.grupoj.Razonando_Back.repository;

import com.grupoj.Razonando_Back.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    @Query("""
            SELECT DISTINCT auth
            FROM Auth auth
            JOIN FETCH auth.user user
            LEFT JOIN FETCH user.usersRoles userRole
            LEFT JOIN FETCH userRole.role
            WHERE user.email = :email
              AND auth.passHash IS NOT NULL
          """)
    Optional<Auth> findPasswordAuthByEmail(@Param("email") String email);
}
