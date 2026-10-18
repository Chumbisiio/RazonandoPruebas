package com.grupoj.Razonando_Back.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grupoj.Razonando_Back.dto.CourseAdminRequests.CourseDetailsRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.CourseNameRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.ModuleRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.SubjectRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.TeacherAssignmentRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.TopicRequest;
import com.grupoj.Razonando_Back.dto.CourseResponse;
import com.grupoj.Razonando_Back.dto.TeacherResponse;
import com.grupoj.Razonando_Back.service.CourseAdminService;
import com.grupoj.Razonando_Back.service.CourseService;

import jakarta.validation.Valid;

/**
 * Creación y edición de cursos. Rutas bajo /api/admin: acceso para ADMIN y SUPERADMIN (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin")
public class CourseAdminController {

    private final CourseAdminService adminService;
    private final CourseService courseService;

    public CourseAdminController(CourseAdminService adminService, CourseService courseService) {
        this.adminService = adminService;
        this.courseService = courseService;
    }

    // ---------- Curso ----------

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CourseNameRequest request) {
        Long courseId = adminService.createCourse(request.name());
        return courseService.getCourseResponse(courseId);
    }

    @PutMapping("/courses/{courseId}")
    public void renameCourse(@PathVariable("courseId") Long courseId, @Valid @RequestBody CourseNameRequest request) {
        adminService.renameCourse(courseId, request.name());
    }

    @PutMapping("/courses/{courseId}/details")
    public void updateDetails(@PathVariable("courseId") Long courseId, @Valid @RequestBody CourseDetailsRequest request) {
        adminService.updateDetails(courseId, request);
    }

    @PostMapping("/courses/{courseId}/publish")
    public void publish(@PathVariable("courseId") Long courseId) {
        adminService.publish(courseId);
    }

    @DeleteMapping("/courses/{courseId}")
    public void deleteCourse(@PathVariable("courseId") Long courseId) {
        adminService.deleteCourse(courseId);
    }

    // ---------- Profesores ----------

    @GetMapping("/teachers")
    public List<TeacherResponse> teachers() {
        return adminService.getTeachers();
    }

    @PutMapping("/course-subjects/{courseSubjectId}/teacher")
    public void assignTeacher(@PathVariable("courseSubjectId") Long courseSubjectId, @RequestBody TeacherAssignmentRequest request) {
        adminService.assignTeacher(courseSubjectId, request.teacherId());
    }

    // ---------- Materias ----------

    @PostMapping("/courses/{courseId}/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSubject(@PathVariable("courseId") Long courseId, @Valid @RequestBody SubjectRequest request) {
        adminService.addSubject(courseId, request);
    }

    @PutMapping("/course-subjects/{courseSubjectId}")
    public void updateSubject(@PathVariable("courseSubjectId") Long courseSubjectId, @Valid @RequestBody SubjectRequest request) {
        adminService.updateSubject(courseSubjectId, request);
    }

    @DeleteMapping("/course-subjects/{courseSubjectId}")
    public void deleteSubject(@PathVariable("courseSubjectId") Long courseSubjectId) {
        adminService.deleteSubject(courseSubjectId);
    }

    // ---------- Módulos ----------

    @PostMapping("/course-subjects/{courseSubjectId}/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public void addModule(@PathVariable("courseSubjectId") Long courseSubjectId, @Valid @RequestBody ModuleRequest request) {
        adminService.addModule(courseSubjectId, request);
    }

    @PutMapping("/modules/{moduleId}")
    public void updateModule(@PathVariable("moduleId") Long moduleId, @Valid @RequestBody ModuleRequest request) {
        adminService.updateModule(moduleId, request);
    }

    @DeleteMapping("/modules/{moduleId}")
    public void deleteModule(@PathVariable("moduleId") Long moduleId) {
        adminService.deleteModule(moduleId);
    }

    // ---------- Temas ----------

    @PostMapping("/modules/{moduleId}/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTopic(@PathVariable("moduleId") Long moduleId, @Valid @RequestBody TopicRequest request) {
        adminService.addTopic(moduleId, request);
    }

    @PutMapping("/topics/{topicId}")
    public void updateTopic(@PathVariable("topicId") Long topicId, @Valid @RequestBody TopicRequest request) {
        adminService.updateTopic(topicId, request);
    }

    @DeleteMapping("/topics/{topicId}")
    public void deleteTopic(@PathVariable("topicId") Long topicId) {
        adminService.deleteTopic(topicId);
    }
}