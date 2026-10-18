package com.grupoj.Razonando_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "files")
@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor  
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = true)
    private Modules module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courses_subjects_id", nullable = true)
    private CoursesSubjects courseSubject;
    
    @Column(name = "file_type", nullable = false, length = 50)
    @Size(max = 50)
    private String fileType;

    @Column(name = "file_name", nullable = false, length = 100)
    @Size(max = 100)
    private String fileName;
    
    @Column(name = "file_url", nullable = false, length = 255)
    @Size(max = 255)
    private String fileUrl;

    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Files that = (Files) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
