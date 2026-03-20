package com.admin.edu_track.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name="academic_year")
@Data
public class AcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Yıl etiketi boş olamaz")
    @Pattern(regexp = "^20\\d{2}-20\\d{2}$",
            message = "Yıl formatı '20XX-20XX' şeklinde olmalıdır (Örn: 2024-2025)")
    @Column(nullable = false, unique = true)
    private String label; // "2024-2025"
    //@Column(name = "active") if we wanted a different column name
    private boolean isActive;

    public static boolean isSequentialYear(String academicYear) {

        String[] years = academicYear.split("-");

        int first = Integer.parseInt(years[0]);
        int second = Integer.parseInt(years[1]);

        return second == first + 1;
    }
}
