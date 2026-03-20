package com.admin.edu_track.services;

import com.admin.edu_track.entities.Student;
import com.admin.edu_track.entities.StudentRegistry;
import com.admin.edu_track.repositories.AcademicYearRepository;
import com.admin.edu_track.repositories.SchoolClassRepository;

import com.admin.edu_track.requests.StudentEnrollmentRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentEnrollmentService {
    private final SchoolClassRepository classRepo;
    private final AcademicYearRepository yearRepo;
    private final StudentService studentService;
    private final StudentRegistryService registryService;
    private final ExcelService excelService;


    @Transactional
    public StudentRegistry enrollNewStudent(StudentEnrollmentRequestDto dto){
        // 1. Create and save student
        Student student = new Student();
        student.setName(dto.getStudentName());
        student.setSurname(dto.getStudentSurname());
        student.setStudentNumber(dto.getStudentNumber());
        Student savedStudent = studentService.saveStudent(student);

        // 2. Registry nesnesini "hafif" şekilde oluştur (Sadece ID'ler ile)
        StudentRegistry registry = new StudentRegistry();
        registry.setStudent(savedStudent);
        registry.setSchoolClass(classRepo.getReferenceById(dto.getClassId()));
        registry.setAcademicYear(yearRepo.getReferenceById(dto.getAcademicYearId()));
        return registryService.createRegistry(registry);

    }

    @Transactional
    public void saveStudentsFromExcel(MultipartFile file, Long classId, Long yearId) {
        try {
            List<Student> studentsFromExcel = excelService.parseStudentExcel(file.getInputStream());

            // Opsiyonel: Veritabanında aynı numaralı öğrenci var mı kontrolü yapılabilir
            for (Student s : studentsFromExcel) {
                // 2. Her öğrenci için bir kayıt DTO'su oluştur
                StudentEnrollmentRequestDto enrollmentDto = new StudentEnrollmentRequestDto();
                enrollmentDto.setStudentName(s.getName());
                enrollmentDto.setStudentSurname(s.getSurname());
                enrollmentDto.setStudentNumber(s.getStudentNumber());
                enrollmentDto.setClassId(classId); // UI'dan gelen sabit ID
                enrollmentDto.setAcademicYearId(yearId); // UI'dan gelen sabit ID

                // 3. Mevcut servisi kullanarak kaydı gerçekleştir
                enrollNewStudent(enrollmentDto);
            }
        } catch (IOException e) {
            throw new RuntimeException("Excel işlenirken hata oluştu: " + e.getMessage());
        }
    }
}
