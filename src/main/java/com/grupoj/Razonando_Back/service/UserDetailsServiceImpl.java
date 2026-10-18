package com.grupoj.Razonando_Back.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.grupoj.Razonando_Back.model.Auth;
import com.grupoj.Razonando_Back.model.UserStates;
import com.grupoj.Razonando_Back.model.UsersRoles;
import com.grupoj.Razonando_Back.model.Roles;
import com.grupoj.Razonando_Back.repository.AuthRepository;

import java.util.Objects;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = authRepository.findPasswordAuthByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontraron credenciales para el correo proporcionado"));

        var authorities = auth.getUser().getUsersRoles().stream()
                .map(UsersRoles::getRole)
                .filter(Objects::nonNull)
                .map(Roles::getName)
                .filter(Objects::nonNull)
                .map(name -> "ROLE_" + name)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserStates state = auth.getUser().getState();
        boolean accountEnabled = UserStates.ACTIVE == state || UserStates.INACTIVE == state;

        return User.withUsername(auth.getUser().getEmail())
                .password(auth.getPassHash())
                .authorities(authorities)
                .disabled(!accountEnabled)
                .build();
    }
}
