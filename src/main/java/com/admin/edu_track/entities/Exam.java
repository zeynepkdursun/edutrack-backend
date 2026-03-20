package com.admin.edu_track.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name="exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title; // "Ozdebir Kasim" veya "Genel Deneme"
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private int level;  // 5, 6, 7 veya 8 (Hangi seviyeye yapıldığı)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="academic_year_id", nullable = false)
    private AcademicYear academicYear;


}
