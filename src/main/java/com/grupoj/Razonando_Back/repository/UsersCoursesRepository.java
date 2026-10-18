package com.grupoj.Razonando_Back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grupoj.Razonando_Back.model.UsersCourses;

public interface UsersCoursesRepository extends JpaRepository<UsersCourses, Long> {

    @EntityGraph(attributePaths = {"course"})
    List<UsersCourses> findByStudent_EmailOrderByCourse_NameAsc(String email);

    boolean existsByStudent_EmailAndCourse_Id(String email, Long courseId);

    boolean existsByCourse_Id(Long courseId);
}