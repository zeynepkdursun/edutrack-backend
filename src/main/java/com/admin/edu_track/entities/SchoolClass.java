package com.admin.edu_track.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name="school_class", uniqueConstraints = {
        // Aynı yıl içinde aynı seviye ve şubeden sadece bir tane olabilir
        @UniqueConstraint(name = "unique_level_branch_year", columnNames = {"level", "branch", "academic_year_id"})
})
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int level;
    @Pattern(regexp = "^[A-Z]$", message = "Branch must be a single letter")
    @Column(length = 1)
    private String branch;
    @ManyToOne
    @JoinColumn(name = "academic_year_id"/*, nullable = false*/)
    private AcademicYear academicYear;

    /*@ManyToOne
    @JoinColumn(name="school_id", nullable=true)
    private School school;*/



}
