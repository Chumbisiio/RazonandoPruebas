package com.grupoj.Razonando_Back.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "courses")
@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor   
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    @Size(max = 50)
    private String name;

    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "registration_start_date", nullable = true, length = 20)
    private LocalDateTime registrationStartDate;

    @Column(name = "registration_end_date", nullable = true, length = 20)
    private LocalDateTime registrationEndDate;

    @Column(name = "start_date", nullable = true, length = 20)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = true, length = 20)
    private LocalDateTime endDate;

    @Column(name = "state", nullable = false, length = 20)
    @Size(max = 20)
    private String state;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Courses that = (Courses) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
