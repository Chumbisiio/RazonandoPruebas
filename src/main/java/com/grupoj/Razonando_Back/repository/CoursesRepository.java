package com.grupoj.Razonando_Back.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupoj.Razonando_Back.model.Courses;

public interface CoursesRepository extends JpaRepository<Courses, Long> {

    // Cursos publicados con inscripciones abiertas en este momento y en los que el estudiante aún no está inscrito
    @Query("""
            select c from Courses c
            where c.state = 'PUBLISHED'
              and c.registrationStartDate <= :now
              and c.registrationEndDate >= :now
              and c.id not in (
                  select uc.course.id from UsersCourses uc where uc.student.email = :email
              )
            order by c.registrationEndDate asc, c.name asc
            """)
    List<Courses> findOpenForRegistration(@Param("email") String email, @Param("now") LocalDateTime now);

    // Cursos en los que el profesor tiene al menos una materia asignada
    @Query("""
            select distinct c from CoursesSubjectsTeachers cst
            join cst.courseSubject cs
            join cs.course c
            where cst.teacher.email = :email
            order by c.name asc
            """)
    List<Courses> findByTeacherEmail(@Param("email") String email);

    List<Courses> findAllByOrderByNameAsc();
}