package com.grupoj.Razonando_Back.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grupoj.Razonando_Back.dto.LoginRequest;
import com.grupoj.Razonando_Back.dto.LoginResponse;
import com.grupoj.Razonando_Back.dto.StudentRegistrationRequest;
import com.grupoj.Razonando_Back.dto.StudentRegistrationResponse;
import com.grupoj.Razonando_Back.model.Auth;
import com.grupoj.Razonando_Back.model.Roles;
import com.grupoj.Razonando_Back.model.UserStates;
import com.grupoj.Razonando_Back.model.Users;
import com.grupoj.Razonando_Back.model.UsersRoles;
import com.grupoj.Razonando_Back.repository.AuthRepository;
import com.grupoj.Razonando_Back.repository.RolesRepository;
import com.grupoj.Razonando_Back.repository.UserRolesRepository;
import com.grupoj.Razonando_Back.repository.UsersRepository;

@Service
@Transactional
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final AuthRepository authRepository;
    private final RolesRepository rolesRepository;
    private final UserRolesRepository userRolesRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsersRepository usersRepository,
            AuthRepository authRepository,
            RolesRepository rolesRepository,
            UserRolesRepository userRolesRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.authRepository = authRepository;
        this.rolesRepository = rolesRepository;
        this.userRolesRepository = userRolesRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo electrónico o contraseña incorrectos");
        }

        if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new IllegalStateException("La autenticación no devolvió un usuario válido");
        }

        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Las credenciales no están asociadas a un usuario"));

        String token = jwtService.generateToken(userDetails);
        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                user.getEmail(),
                user.getNames(),
                user.getNickname(),
                user.getUsersRoles().stream()
                        .map(usersRole -> usersRole.getRole().getName())
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<StudentRegistrationResponse> registerStudent(
            StudentRegistrationRequest registrationRequest) {

        String nickname = registrationRequest.nickname();
        if (nickname != null && nickname.isBlank()) {
            nickname = null;
        }

        if (usersRepository.existsByEmail(registrationRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta registrada con ese correo electrónico");
        } else if (usersRepository.existsByIdNumber(registrationRequest.idNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta registrada con ese número de documento");
        } else if (nickname != null && usersRepository.existsByNickname(nickname)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ese nombre de usuario ya está en uso");
        } 

        Roles studentRole = rolesRepository.findByName("STUDENT")
                .orElseThrow(() -> new IllegalStateException(
                        "El rol STUDENT no está configurado en la base de datos"));

        Users user = Users.builder()
                .documentType(registrationRequest.documentType())
                .idNumber(registrationRequest.idNumber())
                .names(registrationRequest.names())
                .surNames(registrationRequest.surNames())
                .nickname(nickname)
                .email(registrationRequest.email())
                .state(UserStates.ACTIVE)
                .creationDate(LocalDateTime.now())
                .build();
        user = usersRepository.save(user);

        userRolesRepository.save(UsersRoles.builder()
                .user(user)
                .role(studentRole)
                .build());

        authRepository.save(Auth.builder()
                .user(user)
                .supplier("LOCAL")
                .passHash(passwordEncoder.encode(registrationRequest.password()))
                .build());

        StudentRegistrationResponse response = new StudentRegistrationResponse(
                "Usuario registrado correctamente",
                user.getEmail(),
                user.getNames(),
                user.getNickname()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
