package com.admin.edu_track.controllers;

import com.admin.edu_track.entities.StudentRegistry;
import com.admin.edu_track.requests.StudentEnrollmentRequestDto;
import com.admin.edu_track.services.StudentEnrollmentService;
import com.admin.edu_track.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class StudentEnrollmentController {
    private final StudentEnrollmentService enrollmentService;
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentRegistry> enrollNewStudent(@Valid @RequestBody StudentEnrollmentRequestDto dto) {
       return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollNewStudent(dto));
    }

    // Controller'da dosyayı ve sabit bilgileri aynı anda alıyoruz
    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("classId") Long classId,
            @RequestParam("academicYearId") Long academicYearId) {

        enrollmentService.saveStudentsFromExcel(file, classId, academicYearId);
        return ResponseEntity.ok("Öğrenciler başarıyla ilgili sınıfa kaydedildi.");
    }
}
/// AYRICA DTOYA GEREK YOK DTO'YU KALDIR ENROLLMENT SERVİCE'İNDEKİ