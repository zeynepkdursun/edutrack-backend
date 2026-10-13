package com.admin.edu_track.services;

import com.admin.edu_track.entities.Student;
import com.admin.edu_track.exceptions.ResourceNotFoundException;
import com.admin.edu_track.repositories.StudentRegistryRepository;
import com.admin.edu_track.repositories.StudentRepository;
import com.admin.edu_track.responseDto.StudentResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepo;
    private final StudentRegistryRepository registryRepo;


    /// RETURNS ALL STUDENT DTO'S
    public List<StudentResponseDto> getAllStudentsDto(boolean activeOnly) {
        return studentRepo.findAllStudents(activeOnly);
    }

    /// GET METHODS
    public Student getStudentById(Long studentId){
        return studentRepo.findById(studentId).orElseThrow(
                () -> new ResourceNotFoundException("Student not found with the id: " + studentId )
        );
    }

    public List<Student> searchStudents(Long yearId, Integer level, String studentNum){
        return registryRepo.searchStudentsForExam(yearId, level, studentNum);
    }

    public List<Student> getCurrentStudentsByClassId(Long classId){
        return registryRepo.findCurrentStudentsBySchoolClassId(classId);
    }

    public Student getStudentByStudentNumberAndYearId(String studentNo, Long yearId){
        return studentRepo.findByStudentNumberAndYearId(studentNo, yearId);
    }

    /// POST METHOD
    @Transactional
    public Student saveStudent(Student newStudent){
        return studentRepo.save(newStudent);
    }

    /// PUT METHOD
    @Transactional
    public Student updateStudent(Long studentId, Student newStudent){
        return studentRepo.findById(studentId)
                .map(existingStudent -> {
                    existingStudent.setName(newStudent.getName());
                    existingStudent.setSurname(newStudent.getSurname());
                    existingStudent.setStudentNumber(newStudent.getStudentNumber());
                    return studentRepo.save(existingStudent);
                })
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
    }

    @Transactional
    public void deleteStudent(Long studentId){
        Student student = studentRepo.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
        if (registryRepo.existsByStudentId(studentId)) {
            throw new IllegalStateException("student with registry data cannot be deleted, first delete the registry records.");
        }
        studentRepo.delete(student);
    }

}
