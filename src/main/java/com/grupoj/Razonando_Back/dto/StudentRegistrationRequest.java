package com.grupoj.Razonando_Back.dto;

import com.grupoj.Razonando_Back.model.DocumentTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Digits;

public record StudentRegistrationRequest(
    @NotNull(message = "El tipo de documento no puede ser nulo")
    DocumentTypes documentType,
    @NotBlank(message = "El número de documento no puede estar vacío")
    @Digits(integer = 15, fraction = 0, message = "El número de documento debe ser un número válido")
    @Size(max = 15, message = "El número de documento no puede tener más de 15 caracteres")
    String idNumber,
    @NotBlank(message = "Los nombres no pueden estar vacíos")
    @Size(max = 80, message = "Los nombres no pueden tener más de 80 caracteres")
    String names,
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(max = 80, message = "Los apellidos no pueden tener más de 80 caracteres")
    String surNames,
    @Size(max = 50, message = "El apodo no puede tener más de 50 caracteres")
    String nickname,
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Size(max = 80, message = "El correo electrónico no puede tener más de 80 caracteres")
    @Email(message = "El correo electrónico debe tener un formato válido")
    String email,
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!¡?¿/]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    )
    String password
) {

}
