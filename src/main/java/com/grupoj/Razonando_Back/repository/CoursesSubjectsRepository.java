package com.grupoj.Razonando_Back.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupoj.Razonando_Back.model.CoursesSubjects;

public interface CoursesSubjectsRepository extends JpaRepository<CoursesSubjects, Long> {

    // Devuelve pares [courseId, cantidad de materias]
    @Query("""
            select cs.course.id, count(cs) from CoursesSubjects cs
            where cs.course.id in :courseIds
            group by cs.course.id
            """)
    List<Object[]> countSubjectsByCourseIds(@Param("courseIds") Collection<Long> courseIds);

    // Materias de un curso, con la materia ya cargada
    @EntityGraph(attributePaths = {"subject"})
    List<CoursesSubjects> findByCourse_IdOrderBySubject_NameAsc(Long courseId);

    long countByCourse_Id(Long courseId);

    boolean existsBySubject_Id(Long subjectId);

    // Materias del curso que todavía no tienen ningún módulo
    @Query("""
            select count(cs) from CoursesSubjects cs
            where cs.course.id = :courseId
              and not exists (select 1 from Modules m where m.courseSubject.id = cs.id)
            """)
    long countWithoutModules(@Param("courseId") Long courseId);
}