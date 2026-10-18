package com.grupoj.Razonando_Back.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupoj.Razonando_Back.dto.CourseDetailResponse;
import com.grupoj.Razonando_Back.dto.CourseResponse;
import com.grupoj.Razonando_Back.service.CourseService;
import com.grupoj.Razonando_Back.service.EnrollmentService;

/**
 * Listados de cursos. El acceso por rol lo controla SecurityConfig según el prefijo de la ruta.
 * El correo del usuario viene en el "subject" del JWT.
 */
@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/student/courses")
    public List<CourseResponse> studentCourses(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getStudentCourses(jwt.getSubject());
    }

    @GetMapping("/student/courses/open")
    public List<CourseResponse> openCourses(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getOpenCourses(jwt.getSubject());
    }

    // Inscripción del estudiante en un curso con inscripciones abiertas
    @PostMapping("/student/courses/{courseId}/enroll")
    public ResponseEntity<Void> enroll(@PathVariable("courseId") Long courseId, @AuthenticationPrincipal Jwt jwt) {
        enrollmentService.enroll(courseId, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/teacher/courses")
    public List<CourseResponse> teacherCourses(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getTeacherCourses(jwt.getSubject());
    }

    @GetMapping("/admin/courses")
    public List<CourseResponse> adminCourses() {
        return courseService.getAllCourses();
    }

    // Detalle de un curso (materias, módulos y temas). El servicio valida el acceso según el rol.
    @GetMapping("/courses/{courseId}")
    public CourseDetailResponse courseDetail(
            @PathVariable("courseId") Long courseId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return courseService.getCourseDetail(courseId, jwt.getSubject(), roles);
    }
}