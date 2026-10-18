package com.grupoj.Razonando_Back.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.grupoj.Razonando_Back.dto.CourseAdminRequests.CourseDetailsRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.ModuleRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.SubjectRequest;
import com.grupoj.Razonando_Back.dto.CourseAdminRequests.TopicRequest;
import com.grupoj.Razonando_Back.dto.TeacherResponse;
import com.grupoj.Razonando_Back.model.CourseStates;
import com.grupoj.Razonando_Back.model.Courses;
import com.grupoj.Razonando_Back.model.CoursesSubjects;
import com.grupoj.Razonando_Back.model.CoursesSubjectsTeachers;
import com.grupoj.Razonando_Back.model.Modules;
import com.grupoj.Razonando_Back.model.Subjects;
import com.grupoj.Razonando_Back.model.Topics;
import com.grupoj.Razonando_Back.model.UserStates;
import com.grupoj.Razonando_Back.model.Users;
import com.grupoj.Razonando_Back.repository.CoursesRepository;
import com.grupoj.Razonando_Back.repository.CoursesSubjectsRepository;
import com.grupoj.Razonando_Back.repository.CoursesSubjectsTeachersRepository;
import com.grupoj.Razonando_Back.repository.ModulesRepository;
import com.grupoj.Razonando_Back.repository.SubjectsRepository;
import com.grupoj.Razonando_Back.repository.TopicsRepository;
import com.grupoj.Razonando_Back.repository.UsersCoursesRepository;
import com.grupoj.Razonando_Back.repository.UsersRepository;

/**
 * Creación y edición de cursos por administradores y superadministradores.
 * Un curso nace como borrador; descripción y fechas solo se pueden definir cuando la estructura está completa.
 */
@Service
@Transactional
public class CourseAdminService {

    private final CoursesRepository coursesRepository;
    private final SubjectsRepository subjectsRepository;
    private final CoursesSubjectsRepository coursesSubjectsRepository;
    private final CoursesSubjectsTeachersRepository coursesSubjectsTeachersRepository;
    private final ModulesRepository modulesRepository;
    private final TopicsRepository topicsRepository;
    private final UsersRepository usersRepository;
    private final UsersCoursesRepository usersCoursesRepository;
    private final CourseService courseService;

    public CourseAdminService(
            CoursesRepository coursesRepository,
            SubjectsRepository subjectsRepository,
            CoursesSubjectsRepository coursesSubjectsRepository,
            CoursesSubjectsTeachersRepository coursesSubjectsTeachersRepository,
            ModulesRepository modulesRepository,
            TopicsRepository topicsRepository,
            UsersRepository usersRepository,
            UsersCoursesRepository usersCoursesRepository,
            CourseService courseService) {
        this.coursesRepository = coursesRepository;
        this.subjectsRepository = subjectsRepository;
        this.coursesSubjectsRepository = coursesSubjectsRepository;
        this.coursesSubjectsTeachersRepository = coursesSubjectsTeachersRepository;
        this.modulesRepository = modulesRepository;
        this.topicsRepository = topicsRepository;
        this.usersRepository = usersRepository;
        this.usersCoursesRepository = usersCoursesRepository;
        this.courseService = courseService;
    }

    // ---------- Curso ----------

    public Long createCourse(String name) {
        Courses course = Courses.builder()
                .name(name.trim())
                .description("")
                .state(CourseStates.DRAFT)
                .build();
        return coursesRepository.save(course).getId();
    }

    public void renameCourse(Long courseId, String name) {
        Courses course = findCourse(courseId);
        course.setName(name.trim());
        coursesRepository.save(course);
    }

    public void updateDetails(Long courseId, CourseDetailsRequest request) {
        Courses course = findCourse(courseId);

        if (!courseService.isStructureComplete(courseId)) {
            throw conflict("Define al menos una materia, con sus módulos y temas, "
                    + "antes de asignar la descripción y las fechas del curso.");
        }

        validateDates(request.registrationStartDate(), request.registrationEndDate(),
                request.startDate(), request.endDate());

        course.setDescription(request.description().trim());
        course.setRegistrationStartDate(request.registrationStartDate());
        course.setRegistrationEndDate(request.registrationEndDate());
        course.setStartDate(request.startDate());
        course.setEndDate(request.endDate());
        coursesRepository.save(course);
    }

    public void publish(Long courseId) {
        Courses course = findCourse(courseId);

        if (!courseService.isStructureComplete(courseId)) {
            throw conflict("Para publicar, el curso necesita al menos una materia, y cada materia "
                    + "al menos un módulo con al menos un tema.");
        }

        boolean missingDetails = course.getDescription() == null
                || course.getDescription().isBlank()
                || course.getRegistrationStartDate() == null
                || course.getRegistrationEndDate() == null
                || course.getStartDate() == null
                || course.getEndDate() == null;
        if (missingDetails) {
            throw conflict("Para publicar, define la descripción y las cuatro fechas del curso.");
        }

        validateDates(course.getRegistrationStartDate(), course.getRegistrationEndDate(),
                course.getStartDate(), course.getEndDate());

        course.setState(CourseStates.PUBLISHED);
        coursesRepository.save(course);
    }

    // Elimina el curso con toda su estructura. No se permite si tiene estudiantes inscritos.
    public void deleteCourse(Long courseId) {
        Courses course = findCourse(courseId);

        if (usersCoursesRepository.existsByCourse_Id(courseId)) {
            throw conflict("No se puede eliminar un curso que tiene estudiantes inscritos.");
        }

        for (CoursesSubjects courseSubject
                : coursesSubjectsRepository.findByCourse_IdOrderBySubject_NameAsc(courseId)) {
            removeCourseSubject(courseSubject);
        }

        coursesRepository.delete(course);
        coursesRepository.flush();
    }

    // ---------- Profesores ----------

    @Transactional(readOnly = true)
    public List<TeacherResponse> getTeachers() {
        return usersRepository.findByUsersRoles_Role_Name("TEACHER").stream()
                .filter(user -> user.getState() == UserStates.ACTIVE)
                .sorted(Comparator.comparing(Users::getNames).thenComparing(Users::getSurNames))
                .map(user -> new TeacherResponse(user.getId(), user.getNames(), user.getSurNames(), user.getEmail()))
                .toList();
    }

    // teacherId null = quitar el profesor de la materia
    public void assignTeacher(Long courseSubjectId, Long teacherId) {
        applyTeacher(findCourseSubject(courseSubjectId), teacherId);
    }

    private void applyTeacher(CoursesSubjects courseSubject, Long teacherId) {
        coursesSubjectsTeachersRepository.deleteByCourseSubject_Id(courseSubject.getId());
        coursesSubjectsTeachersRepository.flush();

        if (teacherId == null) {
            return;
        }

        Users teacher = usersRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El profesor no existe"));

        boolean isTeacher = teacher.getUsersRoles().stream()
                .anyMatch(userRole -> "TEACHER".equals(userRole.getRole().getName()));
        if (!isTeacher) {
            throw badRequest("El usuario seleccionado no tiene el rol de profesor.");
        }

        coursesSubjectsTeachersRepository.save(CoursesSubjectsTeachers.builder()
                .courseSubject(courseSubject)
                .teacher(teacher)
                .build());
    }

    // ---------- Materias ----------

    public void addSubject(Long courseId, SubjectRequest request) {
        Courses course = findCourse(courseId);

        Subjects subject = subjectsRepository.save(Subjects.builder()
                .name(request.name().trim())
                .description(blankToEmpty(request.description()))
                .build());

        CoursesSubjects courseSubject = coursesSubjectsRepository.save(CoursesSubjects.builder()
                .course(course)
                .subject(subject)
                .build());

        applyTeacher(courseSubject, request.teacherId());
    }

    public void updateSubject(Long courseSubjectId, SubjectRequest request) {
        Subjects subject = findCourseSubject(courseSubjectId).getSubject();
        subject.setName(request.name().trim());
        subject.setDescription(blankToEmpty(request.description()));
        subjectsRepository.save(subject);
    }

    public void deleteSubject(Long courseSubjectId) {
        CoursesSubjects courseSubject = findCourseSubject(courseSubjectId);
        Long courseId = courseSubject.getCourse().getId();

        removeCourseSubject(courseSubject);

        ensureCourseKeepsContent(courseId);
    }

    // Borra una materia del curso con sus módulos, temas y profesores asignados
    private void removeCourseSubject(CoursesSubjects courseSubject) {
        Long courseSubjectId = courseSubject.getId();
        Subjects subject = courseSubject.getSubject();

        for (Modules module : modulesRepository.findByCourseSubject_Id(courseSubjectId)) {
            topicsRepository.deleteByModule_Id(module.getId());
            modulesRepository.delete(module);
        }
        coursesSubjectsTeachersRepository.deleteByCourseSubject_Id(courseSubjectId);
        coursesSubjectsRepository.delete(courseSubject);
        coursesSubjectsRepository.flush();

        // La materia solo se borra si ningún otro curso la usa
        if (!coursesSubjectsRepository.existsBySubject_Id(subject.getId())) {
            subjectsRepository.delete(subject);
            subjectsRepository.flush();
        }
    }

    // ---------- Módulos ----------

    public void addModule(Long courseSubjectId, ModuleRequest request) {
        CoursesSubjects courseSubject = findCourseSubject(courseSubjectId);

        int nextOrder = modulesRepository.findByCourseSubject_Id(courseSubjectId).stream()
                .mapToInt(Modules::getOrder)
                .max()
                .orElse(0) + 1;

        modulesRepository.save(Modules.builder()
                .name(request.name().trim())
                .summary(blankToEmpty(request.summary()))
                .courseSubject(courseSubject)
                .order(nextOrder)
                .build());
    }

    public void updateModule(Long moduleId, ModuleRequest request) {
        Modules module = findModule(moduleId);
        module.setName(request.name().trim());
        module.setSummary(blankToEmpty(request.summary()));
        modulesRepository.save(module);
    }

    public void deleteModule(Long moduleId) {
        Modules module = findModule(moduleId);
        Long courseId = module.getCourseSubject().getCourse().getId();

        topicsRepository.deleteByModule_Id(moduleId);
        modulesRepository.delete(module);
        modulesRepository.flush();

        ensureCourseKeepsContent(courseId);
    }

    // ---------- Temas ----------

    public void addTopic(Long moduleId, TopicRequest request) {
        Modules module = findModule(moduleId);

        // El contenido lo sube después el profesor asignado
        topicsRepository.save(Topics.builder()
                .name(request.name().trim())
                .module(module)
                .content("")
                .build());
    }

    public void updateTopic(Long topicId, TopicRequest request) {
        Topics topic = findTopic(topicId);
        topic.setName(request.name().trim());
        topicsRepository.save(topic);
    }

    public void deleteTopic(Long topicId) {
        Topics topic = findTopic(topicId);
        Long courseId = topic.getModule().getCourseSubject().getCourse().getId();

        topicsRepository.delete(topic);
        topicsRepository.flush();

        ensureCourseKeepsContent(courseId);
    }

    // ---------- Utilidades ----------

    // Un curso publicado no puede quedarse sin ningún tema
    private void ensureCourseKeepsContent(Long courseId) {
        Courses course = findCourse(courseId);
        if (CourseStates.PUBLISHED.equals(course.getState()) && topicsRepository.countByCourseId(courseId) == 0) {
            throw conflict("Un curso publicado debe conservar al menos un tema.");
        }
    }

    private void validateDates(LocalDateTime registrationStart, LocalDateTime registrationEnd,
            LocalDateTime start, LocalDateTime end) {
        if (!registrationStart.isBefore(registrationEnd)) {
            throw badRequest("El inicio de inscripciones debe ser anterior al fin de inscripciones.");
        }
        if (!start.isBefore(end)) {
            throw badRequest("El inicio del curso debe ser anterior al fin del curso.");
        }
        if (registrationEnd.isAfter(end)) {
            throw badRequest("Las inscripciones no pueden terminar después de que termine el curso.");
        }
    }

    private String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Courses findCourse(Long courseId) {
        return coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso no existe"));
    }

    private CoursesSubjects findCourseSubject(Long courseSubjectId) {
        return coursesSubjectsRepository.findById(courseSubjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La materia no existe"));
    }

    private Modules findModule(Long moduleId) {
        return modulesRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El módulo no existe"));
    }

    private Topics findTopic(Long topicId) {
        return topicsRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El tema no existe"));
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}