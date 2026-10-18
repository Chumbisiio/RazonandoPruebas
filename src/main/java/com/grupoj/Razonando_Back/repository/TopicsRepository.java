package com.grupoj.Razonando_Back.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupoj.Razonando_Back.model.Topics;

public interface TopicsRepository extends JpaRepository<Topics, Long> {

    // Devuelve filas [id, name, moduleId] sin cargar el contenido completo del tema
    @Query("""
            select t.id, t.name, t.module.id from Topics t
            where t.module.id in :moduleIds
            order by t.id asc
            """)
    List<Object[]> findSummariesByModuleIds(@Param("moduleIds") Collection<Long> moduleIds);

    @Query("select count(t) from Topics t where t.module.courseSubject.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    void deleteByModule_Id(Long moduleId);
}