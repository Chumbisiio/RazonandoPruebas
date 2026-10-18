package com.grupoj.Razonando_Back.dto;

import java.util.List;

public record CourseDetailResponse(
    CourseResponse course,
    boolean structureComplete,
    List<SubjectItem> subjects
) {
    public record SubjectItem(
        Long courseSubjectId,
        String name,
        String description,
        TeacherResponse teacher,
        List<ModuleItem> modules
    ) {
    }

    public record ModuleItem(Long id, String name, String summary, List<TopicItem> topics) {
    }

    public record TopicItem(Long id, String name) {
    }
}