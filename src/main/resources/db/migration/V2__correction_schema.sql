ALTER TABLE public.answer_options
    RENAME COLUMN its_correct TO is_correct;

ALTER TABLE public.courses
    RENAME COLUMN registration_star_date TO registration_start_date;

ALTER TABLE public.users
    RENAME COLUMN rol_id TO role_id;

ALTER TABLE public.assessments
    ADD COLUMN start_date timestamp without time zone,
    ADD COLUMN end_date timestamp without time zone;

ALTER TABLE public.assessments
    ADD COLUMN state character varying;

ALTER TABLE public.assessments
    ALTER COLUMN state SET NOT NULL;