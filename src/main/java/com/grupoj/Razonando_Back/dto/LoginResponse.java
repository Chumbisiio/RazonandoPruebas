package com.grupoj.Razonando_Back.dto;

import java.util.List;

public record LoginResponse(
    String token,
    String typeToken,
    String email,
    String names,
    String nickname,
    List<String> roles
) {
    
}
