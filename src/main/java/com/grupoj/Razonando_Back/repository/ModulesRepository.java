package com.grupoj.Razonando_Back.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupoj.Razonando_Back.model.Modules;

public interface ModulesRepository extends JpaRepository<Modules, Long> {

    @Query("""
            select m from Modules m
            join fetch m.courseSubject cs
            where cs.id in :courseSubjectIds
            """)
    List<Modules> findByCourseSubjectIds(@Param("courseSubjectIds") Collection<Long> courseSubjectIds);

    List<Modules> findByCourseSubject_Id(Long courseSubjectId);

    // Módulos del curso que todavía no tienen ningún tema
    @Query("""
            select count(m) from Modules m
            where m.courseSubject.course.id = :courseId
              and not exists (select 1 from Topics t where t.module.id = m.id)
            """)
    long countWithoutTopics(@Param("courseId") Long courseId);
}