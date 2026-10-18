package com.grupoj.Razonando_Back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;


public record LoginRequest(
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    String email,
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password
) {

}
