package com.grupoj.Razonando_Back.repository;

import com.grupoj.Razonando_Back.model.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {
    @EntityGraph(attributePaths = {"usersRoles", "usersRoles.role"})
    Optional<Users> findByEmail(String email);
    Optional<Users> findByIdNumber(String idNumber);
    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    boolean existsByNickname(String nickname);
    List<Users> findByUsersRoles_Role_Name(String roleName);
}
