package com.admin.edu_track.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="student_registry", indexes = {
        @Index(name = "idx_student_year", columnList = "student_id, academic_year_id", unique=true)
})
@Data
public class StudentRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id", nullable=false)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="school_class_id", nullable=false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="academic_year_id", nullable=false)
    private AcademicYear academicYear;




}
