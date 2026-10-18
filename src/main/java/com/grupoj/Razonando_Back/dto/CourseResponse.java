package com.grupoj.Razonando_Back.dto;

import java.time.LocalDateTime;

public record CourseResponse(
    Long id,
    String name,
    String description,
    String state,
    LocalDateTime registrationStartDate,
    LocalDateTime registrationEndDate,
    LocalDateTime startDate,
    LocalDateTime endDate,
    long subjectsCount,
    // Estado de la inscripción del estudiante en el curso (null si no aplica)
    String enrollmentState,
    // NOT_STARTED | IN_PROGRESS | FINISHED según las fechas del curso (null si no hay fechas)
    String phase
) {
}