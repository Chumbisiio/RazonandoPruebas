package com.grupoj.Razonando_Back.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CourseAdminRequests {

    private CourseAdminRequests() {
    }

    public record CourseNameRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String name
    ) {
    }

    public record CourseDetailsRequest(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String description,
        @NotNull(message = "La fecha de inicio de inscripciones es obligatoria")
        LocalDateTime registrationStartDate,
        @NotNull(message = "La fecha de fin de inscripciones es obligatoria")
        LocalDateTime registrationEndDate,
        @NotNull(message = "La fecha de inicio del curso es obligatoria")
        LocalDateTime startDate,
        @NotNull(message = "La fecha de fin del curso es obligatoria")
        LocalDateTime endDate
    ) {
    }

    // teacherId es opcional y solo se usa al crear la materia; para cambiarlo después se usa TeacherAssignmentRequest
    public record SubjectRequest(
        @NotBlank(message = "El nombre de la materia es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String name,
        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String description,
        Long teacherId
    ) {
    }

    // teacherId null = quitar el profesor de la materia
    public record TeacherAssignmentRequest(Long teacherId) {
    }

    public record ModuleRequest(
        @NotBlank(message = "El nombre del módulo es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String name,
        @Size(max = 2000, message = "El resumen no puede superar los 2000 caracteres")
        String summary
    ) {
    }

    public record TopicRequest(
        @NotBlank(message = "El nombre del tema es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String name
    ) {
    }
}