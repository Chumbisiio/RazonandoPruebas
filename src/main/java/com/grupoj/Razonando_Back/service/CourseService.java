package com.grupoj.Razonando_Back.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grupoj.Razonando_Back.dto.CourseDetailResponse;
import com.grupoj.Razonando_Back.dto.CourseResponse;
import com.grupoj.Razonando_Back.dto.TeacherResponse;
import com.grupoj.Razonando_Back.model.CourseStates;
import com.grupoj.Razonando_Back.model.Courses;
import com.grupoj.Razonando_Back.model.CoursesSubjects;
import com.grupoj.Razonando_Back.model.CoursesSubjectsTeachers;
import com.grupoj.Razonando_Back.model.Modules;
import com.grupoj.Razonando_Back.model.Users;
import com.grupoj.Razonando_Back.model.UsersCourses;
import com.grupoj.Razonando_Back.repository.CoursesRepository;
import com.grupoj.Razonando_Back.repository.CoursesSubjectsRepository;
import com.grupoj.Razonando_Back.repository.CoursesSubjectsTeachersRepository;
import com.grupoj.Razonando_Back.repository.ModulesRepository;
import com.grupoj.Razonando_Back.repository.TopicsRepository;
import com.grupoj.Razonando_Back.repository.UsersCoursesRepository;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CoursesRepository coursesRepository;
    private final CoursesSubjectsRepository coursesSubjectsRepository;
    private final CoursesSubjectsTeachersRepository coursesSubjectsTeachersRepository;
    private final ModulesRepository modulesRepository;
    private final TopicsRepository topicsRepository;
    private final UsersCoursesRepository usersCoursesRepository;

    public CourseService(
            CoursesRepository coursesRepository,
            CoursesSubjectsRepository coursesSubjectsRepository,
            CoursesSubjectsTeachersRepository coursesSubjectsTeachersRepository,
            ModulesRepository modulesRepository,
            TopicsRepository topicsRepository,
            UsersCoursesRepository usersCoursesRepository) {
        this.coursesRepository = coursesRepository;
        this.coursesSubjectsRepository = coursesSubjectsRepository;
        this.coursesSubjectsTeachersRepository = coursesSubjectsTeachersRepository;
        this.modulesRepository = modulesRepository;
        this.topicsRepository = topicsRepository;
        this.usersCoursesRepository = usersCoursesRepository;
    }

    /** Cursos en los que el estudiante está inscrito ("Tus cursos"). */
    public List<CourseResponse> getStudentCourses(String email) {
        List<UsersCourses> enrollments = usersCoursesRepository.findByStudent_EmailOrderByCourse_NameAsc(email).stream()
                .filter(enrollment -> CourseStates.PUBLISHED.equals(enrollment.getCourse().getState()))
                .toList();
        List<Courses> courses = enrollments.stream().map(UsersCourses::getCourse).toList();
        Map<Long, Long> counts = countSubjects(courses);

        return enrollments.stream()
                .map(enrollment -> toResponse(enrollment.getCourse(), counts, enrollment.getState()))
                .toList();
    }

    /** Cursos publicados con inscripciones abiertas en los que el estudiante todavía no está inscrito. */
    public List<CourseResponse> getOpenCourses(String email) {
        List<Courses> courses = coursesRepository.findOpenForRegistration(email, LocalDateTime.now());
        return toResponses(courses);
    }

    /** Cursos donde el profesor tiene materias asignadas. */
    public List<CourseResponse> getTeacherCourses(String email) {
        return toResponses(coursesRepository.findByTeacherEmail(email));
    }

    /** Todos los cursos (administradores y superadministradores). */
    public List<CourseResponse> getAllCourses() {
        return toResponses(coursesRepository.findAllByOrderByNameAsc());
    }

    public CourseResponse getCourseResponse(Long courseId) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso no existe"));
        return toResponse(course, countSubjects(List.of(course)), null);
    }

    /** Un curso tiene la estructura completa si tiene materias, cada materia módulos y cada módulo temas. */
    public boolean isStructureComplete(Long courseId) {
        return coursesSubjectsRepository.countByCourse_Id(courseId) > 0
                && coursesSubjectsRepository.countWithoutModules(courseId) == 0
                && modulesRepository.countWithoutTopics(courseId) == 0;
    }

    /** Detalle de un curso: materias, profesores, módulos y temas. Valida que el usuario tenga acceso. */
    public CourseDetailResponse getCourseDetail(Long courseId, String email, Collection<String> roles) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso no existe"));

        if (!canViewCourse(course, email, roles)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este curso");
        }

        List<CoursesSubjects> courseSubjects =
                coursesSubjectsRepository.findByCourse_IdOrderBySubject_NameAsc(courseId);
        List<Long> courseSubjectIds = courseSubjects.stream().map(CoursesSubjects::getId).toList();

        Map<Long, List<Modules>> modulesByCourseSubject = new HashMap<>();
        Map<Long, List<CourseDetailResponse.TopicItem>> topicsByModule = new HashMap<>();
        Map<Long, TeacherResponse> teacherByCourseSubject = new HashMap<>();

        if (!courseSubjectIds.isEmpty()) {
            for (CoursesSubjectsTeachers assignment
                    : coursesSubjectsTeachersRepository.findByCourseSubject_IdIn(courseSubjectIds)) {
                Users teacher = assignment.getTeacher();
                teacherByCourseSubject.putIfAbsent(
                        assignment.getCourseSubject().getId(),
                        new TeacherResponse(teacher.getId(), teacher.getNames(), teacher.getSurNames(),
                                teacher.getEmail()));
            }

            List<Modules> modules = modulesRepository.findByCourseSubjectIds(courseSubjectIds).stream()
                    .sorted(Comparator.comparing(Modules::getOrder))
                    .toList();

            List<Long> moduleIds = new ArrayList<>();
            for (Modules module : modules) {
                moduleIds.add(module.getId());
                modulesByCourseSubject
                        .computeIfAbsent(module.getCourseSubject().getId(), key -> new ArrayList<>())
                        .add(module);
            }

            if (!moduleIds.isEmpty()) {
                for (Object[] row : topicsRepository.findSummariesByModuleIds(moduleIds)) {
                    topicsByModule
                            .computeIfAbsent((Long) row[2], key -> new ArrayList<>())
                            .add(new CourseDetailResponse.TopicItem((Long) row[0], (String) row[1]));
                }
            }
        }

        List<CourseDetailResponse.SubjectItem> subjects = courseSubjects.stream()
                .map(courseSubject -> new CourseDetailResponse.SubjectItem(
                        courseSubject.getId(),
                        courseSubject.getSubject().getName(),
                        courseSubject.getSubject().getDescription(),
                        teacherByCourseSubject.get(courseSubject.getId()),
                        modulesByCourseSubject.getOrDefault(courseSubject.getId(), List.of()).stream()
                                .map(module -> new CourseDetailResponse.ModuleItem(
                                        module.getId(),
                                        module.getName(),
                                        module.getSummary(),
                                        topicsByModule.getOrDefault(module.getId(), List.of())))
                                .toList()))
                .toList();

        CourseResponse courseResponse =
                toResponse(course, Map.of(course.getId(), (long) courseSubjects.size()), null);

        return new CourseDetailResponse(courseResponse, isStructureComplete(courseId), subjects);
    }

    private boolean canViewCourse(Courses course, String email, Collection<String> roles) {
        Long courseId = course.getId();
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_SUPERADMIN")) {
            return true;
        }
        if (roles.contains("ROLE_TEACHER")
                && coursesSubjectsTeachersRepository.existsByCourseSubject_Course_IdAndTeacher_Email(courseId, email)) {
            return true;
        }
        // Un estudiante solo ve cursos publicados en los que está inscrito
        return roles.contains("ROLE_STUDENT")
                && CourseStates.PUBLISHED.equals(course.getState())
                && usersCoursesRepository.existsByStudent_EmailAndCourse_Id(email, courseId);
    }

    private List<CourseResponse> toResponses(List<Courses> courses) {
        Map<Long, Long> counts = countSubjects(courses);
        return courses.stream().map(course -> toResponse(course, counts, null)).toList();
    }

    private Map<Long, Long> countSubjects(Collection<Courses> courses) {
        Map<Long, Long> counts = new HashMap<>();
        if (courses.isEmpty()) {
            return counts;
        }
        List<Long> ids = courses.stream().map(Courses::getId).toList();
        for (Object[] row : coursesSubjectsRepository.countSubjectsByCourseIds(ids)) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }

    private String phaseOf(Courses course) {
        if (course.getStartDate() == null || course.getEndDate() == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(course.getStartDate())) {
            return "NOT_STARTED";
        }
        if (now.isAfter(course.getEndDate())) {
            return "FINISHED";
        }
        return "IN_PROGRESS";
    }

    private CourseResponse toResponse(Courses course, Map<Long, Long> counts, String enrollmentState) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getState(),
                course.getRegistrationStartDate(),
                course.getRegistrationEndDate(),
                course.getStartDate(),
                course.getEndDate(),
                counts.getOrDefault(course.getId(), 0L),
                enrollmentState,
                phaseOf(course));
    }
}