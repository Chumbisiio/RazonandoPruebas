package com.grupoj.Razonando_Back.dto;

public record StudentRegistrationResponse(
    String message,
    String email,
    String names,
    String nickname
) {

}
