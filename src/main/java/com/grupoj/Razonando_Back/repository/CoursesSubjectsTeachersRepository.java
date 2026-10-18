package com.grupoj.Razonando_Back.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grupoj.Razonando_Back.model.CoursesSubjectsTeachers;

public interface CoursesSubjectsTeachersRepository extends JpaRepository<CoursesSubjectsTeachers, Long> {

    boolean existsByCourseSubject_Course_IdAndTeacher_Email(Long courseId, String email);

    void deleteByCourseSubject_Id(Long courseSubjectId);

    @EntityGraph(attributePaths = {"teacher"})
    List<CoursesSubjectsTeachers> findByCourseSubject_IdIn(Collection<Long> courseSubjectIds);
}