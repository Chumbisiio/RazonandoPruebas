-- Eliminar temporalmente las claves foráneas
ALTER TABLE public.answer_options
    DROP CONSTRAINT IF EXISTS answer_options_question_id_fkey;
ALTER TABLE public.assessment_attempts
    DROP CONSTRAINT IF EXISTS assessment_attempts_assessment_id_fkey,
    DROP CONSTRAINT IF EXISTS assessment_attempts_student_id_fkey;
ALTER TABLE public.assessments
    DROP CONSTRAINT IF EXISTS assessments_course_id_fkey,
    DROP CONSTRAINT IF EXISTS assessments_courses_subjects_id_fkey,
    DROP CONSTRAINT IF EXISTS assessments_module_id_fkey,
    DROP CONSTRAINT IF EXISTS assessments_topic_id_fkey;
ALTER TABLE public.auth
    DROP CONSTRAINT IF EXISTS auth_user_id_fkey;
ALTER TABLE public.courses_subjects
    DROP CONSTRAINT IF EXISTS courses_subjects_course_id_fkey,
    DROP CONSTRAINT IF EXISTS courses_subjects_subject_id_fkey;
ALTER TABLE public.files
    DROP CONSTRAINT IF EXISTS files_courses_subjects_id_fkey,
    DROP CONSTRAINT IF EXISTS files_topic_id_fkey;
ALTER TABLE public.modules
    DROP CONSTRAINT IF EXISTS modules_courses_subjects_id_fkey;
ALTER TABLE public.questions
    DROP CONSTRAINT IF EXISTS questions_assessment_id_fkey;
ALTER TABLE public.student_progress
    DROP CONSTRAINT IF EXISTS student_progress_courses_subjects_id_fkey,
    DROP CONSTRAINT IF EXISTS student_progress_current_topic_fkey,
    DROP CONSTRAINT IF EXISTS student_progress_student_id_fkey;
ALTER TABLE public.subjects
    DROP CONSTRAINT IF EXISTS subjects_teacher_id_fkey;
ALTER TABLE public.topics
    DROP CONSTRAINT IF EXISTS topic_module_id_fkey;
ALTER TABLE public.users_courses
    DROP CONSTRAINT IF EXISTS users_courses_course_id_fkey,
    DROP CONSTRAINT IF EXISTS users_courses_student_id_fkey;
ALTER TABLE public.users
    DROP CONSTRAINT IF EXISTS users_rol_id_fkey;

-- Cambiar a BIGINT los IDs de las entidades
ALTER TABLE public.answer_options ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.assessment_attempts ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.assessments ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.auth ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.courses ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.courses_subjects ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.files ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.modules ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.questions ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.roles ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.student_progress ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.subjects ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.topics ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.users ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE public.users_courses ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

-- Cambiar a BIGINT las columnas que referencian esos IDs
ALTER TABLE public.answer_options
    ALTER COLUMN question_id TYPE BIGINT USING question_id::BIGINT;
ALTER TABLE public.assessment_attempts
    ALTER COLUMN assessment_id TYPE BIGINT USING assessment_id::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT;
ALTER TABLE public.assessments
    ALTER COLUMN course_id TYPE BIGINT USING course_id::BIGINT,
    ALTER COLUMN courses_subjects_id TYPE BIGINT USING courses_subjects_id::BIGINT,
    ALTER COLUMN module_id TYPE BIGINT USING module_id::BIGINT,
    ALTER COLUMN topic_id TYPE BIGINT USING topic_id::BIGINT;
ALTER TABLE public.auth
    ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE public.courses_subjects
    ALTER COLUMN course_id TYPE BIGINT USING course_id::BIGINT,
    ALTER COLUMN subject_id TYPE BIGINT USING subject_id::BIGINT;
ALTER TABLE public.files
    ALTER COLUMN courses_subjects_id TYPE BIGINT USING courses_subjects_id::BIGINT,
    ALTER COLUMN topic_id TYPE BIGINT USING topic_id::BIGINT;
ALTER TABLE public.modules
    ALTER COLUMN courses_subjects_id TYPE BIGINT USING courses_subjects_id::BIGINT;
ALTER TABLE public.questions
    ALTER COLUMN assessment_id TYPE BIGINT USING assessment_id::BIGINT;
ALTER TABLE public.student_progress
    ALTER COLUMN courses_subjects_id TYPE BIGINT USING courses_subjects_id::BIGINT,
    ALTER COLUMN current_topic TYPE BIGINT USING current_topic::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT;
ALTER TABLE public.subjects
    ALTER COLUMN teacher_id TYPE BIGINT USING teacher_id::BIGINT;
ALTER TABLE public.topics
    ALTER COLUMN module_id TYPE BIGINT USING module_id::BIGINT;
ALTER TABLE public.users_courses
    ALTER COLUMN course_id TYPE BIGINT USING course_id::BIGINT,
    ALTER COLUMN student_id TYPE BIGINT USING student_id::BIGINT;
ALTER TABLE public.users
    ALTER COLUMN role_id TYPE BIGINT USING role_id::BIGINT;

-- Volver a crear las claves foráneas
ALTER TABLE public.answer_options
    ADD CONSTRAINT answer_options_question_id_fkey
    FOREIGN KEY (question_id) REFERENCES public.questions(id) DEFERRABLE;
ALTER TABLE public.assessment_attempts
    ADD CONSTRAINT assessment_attempts_assessment_id_fkey
    FOREIGN KEY (assessment_id) REFERENCES public.assessments(id) DEFERRABLE,
    ADD CONSTRAINT assessment_attempts_student_id_fkey
    FOREIGN KEY (student_id) REFERENCES public.users(id) DEFERRABLE;
ALTER TABLE public.assessments
    ADD CONSTRAINT assessments_course_id_fkey
    FOREIGN KEY (course_id) REFERENCES public.courses(id) DEFERRABLE,
    ADD CONSTRAINT assessments_courses_subjects_id_fkey
    FOREIGN KEY (courses_subjects_id) REFERENCES public.courses_subjects(id) DEFERRABLE,
    ADD CONSTRAINT assessments_module_id_fkey
    FOREIGN KEY (module_id) REFERENCES public.modules(id) DEFERRABLE,
    ADD CONSTRAINT assessments_topic_id_fkey
    FOREIGN KEY (topic_id) REFERENCES public.topics(id) DEFERRABLE;
ALTER TABLE public.auth
    ADD CONSTRAINT auth_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES public.users(id) DEFERRABLE;
ALTER TABLE public.courses_subjects
    ADD CONSTRAINT courses_subjects_course_id_fkey
    FOREIGN KEY (course_id) REFERENCES public.courses(id) DEFERRABLE,
    ADD CONSTRAINT courses_subjects_subject_id_fkey
    FOREIGN KEY (subject_id) REFERENCES public.subjects(id) DEFERRABLE;
ALTER TABLE public.files
    ADD CONSTRAINT files_courses_subjects_id_fkey
    FOREIGN KEY (courses_subjects_id) REFERENCES public.courses_subjects(id) DEFERRABLE,
    ADD CONSTRAINT files_topic_id_fkey
    FOREIGN KEY (topic_id) REFERENCES public.topics(id) DEFERRABLE;
ALTER TABLE public.modules
    ADD CONSTRAINT modules_courses_subjects_id_fkey
    FOREIGN KEY (courses_subjects_id) REFERENCES public.courses_subjects(id) DEFERRABLE;
ALTER TABLE public.questions
    ADD CONSTRAINT questions_assessment_id_fkey
    FOREIGN KEY (assessment_id) REFERENCES public.assessments(id) DEFERRABLE;
ALTER TABLE public.student_progress
    ADD CONSTRAINT student_progress_courses_subjects_id_fkey
    FOREIGN KEY (courses_subjects_id) REFERENCES public.courses_subjects(id) DEFERRABLE,
    ADD CONSTRAINT student_progress_current_topic_fkey
    FOREIGN KEY (current_topic) REFERENCES public.topics(id) DEFERRABLE,
    ADD CONSTRAINT student_progress_student_id_fkey
    FOREIGN KEY (student_id) REFERENCES public.users(id) DEFERRABLE;
ALTER TABLE public.subjects
    ADD CONSTRAINT subjects_teacher_id_fkey
    FOREIGN KEY (teacher_id) REFERENCES public.users(id) DEFERRABLE;
ALTER TABLE public.topics
    ADD CONSTRAINT topic_module_id_fkey
    FOREIGN KEY (module_id) REFERENCES public.modules(id) DEFERRABLE;
ALTER TABLE public.users_courses
    ADD CONSTRAINT users_courses_course_id_fkey
    FOREIGN KEY (course_id) REFERENCES public.courses(id) DEFERRABLE,
    ADD CONSTRAINT users_courses_student_id_fkey
    FOREIGN KEY (student_id) REFERENCES public.users(id) DEFERRABLE;
ALTER TABLE public.users
    ADD CONSTRAINT users_rol_id_fkey
    FOREIGN KEY (role_id) REFERENCES public.roles(id) DEFERRABLE;