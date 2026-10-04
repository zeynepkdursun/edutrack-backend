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

    @NotBlank(message = "Year label can not be blank")
    @Pattern(regexp = "^20\\d{2}-20\\d{2}$",
            message = "Year format must be like this: '20XX-20XX' (Example: 2024-2025)")
    @Column(nullable = false, unique = true)
    private String label; // "2024-2025"
    //@Column(name = "active") if we wanted a different column name
    private boolean isActive;

}
