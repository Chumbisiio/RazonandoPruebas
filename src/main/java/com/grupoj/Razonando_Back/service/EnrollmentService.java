package com.grupoj.Razonando_Back.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grupoj.Razonando_Back.model.CourseStates;
import com.grupoj.Razonando_Back.model.Courses;
import com.grupoj.Razonando_Back.model.Users;
import com.grupoj.Razonando_Back.model.UsersCourses;
import com.grupoj.Razonando_Back.repository.CoursesRepository;
import com.grupoj.Razonando_Back.repository.UsersCoursesRepository;
import com.grupoj.Razonando_Back.repository.UsersRepository;

@Service
public class EnrollmentService {

    public static final String ENROLLED = "ENROLLED";

    private final CoursesRepository coursesRepository;
    private final UsersRepository usersRepository;
    private final UsersCoursesRepository usersCoursesRepository;

    public EnrollmentService(CoursesRepository coursesRepository,
                             UsersRepository usersRepository,
                             UsersCoursesRepository usersCoursesRepository) {
        this.coursesRepository = coursesRepository;
        this.usersRepository = usersRepository;
        this.usersCoursesRepository = usersCoursesRepository;
    }

    @Transactional
    public void enroll(Long courseId, String email) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso no existe."));

        LocalDateTime now = LocalDateTime.now();
        boolean open = CourseStates.PUBLISHED.equals(course.getState())
                && course.getRegistrationStartDate() != null
                && course.getRegistrationEndDate() != null
                && !now.isBefore(course.getRegistrationStartDate())
                && !now.isAfter(course.getRegistrationEndDate());

        if (!open) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este curso no tiene las inscripciones abiertas.");
        }

        if (usersCoursesRepository.existsByStudent_EmailAndCourse_Id(email, courseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya estás inscrito en este curso.");
        }

        Users student = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));

        usersCoursesRepository.save(UsersCourses.builder()
                .student(student)
                .course(course)
                .state(ENROLLED)
                .build());
    }
}