package com.admin.edu_track.requests;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class StudentEnrollmentRequestDto {
    @NotBlank(message = "Öğrenci adı boş olamaz")
    private String studentName;
    @NotBlank(message = "Öğrenci soyadı boş olamaz")
    private String studentSurname;
    private String studentNumber;
    @NotNull(message = "Sınıf seçimi zorunludur")
    private Long classId;
    @NotNull(message = "Akademik yil seçimi zorunludur")
    private Long academicYearId;
}


