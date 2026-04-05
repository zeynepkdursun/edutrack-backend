package com.admin.edu_track.services;

import com.admin.edu_track.entities.Student;
import com.admin.edu_track.repositories.StudentRegistryRepository;
import com.admin.edu_track.repositories.StudentRepository;
import com.admin.edu_track.responseDto.StudentResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {
    private StudentRepository studentRepo;
    private StudentRegistryRepository registryRepo;


    /// RETURNS ALL STUDENT DTO'S
    public List<StudentResponseDto> getAllStudentsDto() {
        return studentRepo.findAllStudents();
    }
    /// GET METHODS

    public Student getStudentById(Long id){
        return studentRepo.findById(id).orElseThrow(() -> new RuntimeException("Ogrenci bulunamadi"));
    }


    public List<Student> searchStudents(Long yearId, Integer level, String studentNum){
        return registryRepo.searchStudentsForExam(yearId, level, studentNum);
    }
    /// ileride belirli bir yila gore olan sinif listesini getirt!!!!!!
    public List<Student> getStudentsByClassId(Long classId){
        return registryRepo.findCurrentStudentsBySchoolClassId(classId);

        /* return enrollments.stream().map(enrollment -> {
            return enrollment.getStudent();
        }).collect(Collectors.toList());*/

        /*return enrollments.stream()
                .map(StudentRegistry::getStudent) // Method Reference kullanımı
                .collect(Collectors.toList());*/
    }
    //BURADA ONCEKI YILLARDA DA AYNI STUDENTNO'LU OGRENCI VARSA BIRDEN FAZLA STUDENT DONECEK
    //YEAR ID ILE FILTRELE SONRADAN!!!!!!!!!!
    public Student getStudentByStudentNumber(String studentNo){
        return studentRepo.findByStudentNumber(studentNo);
    }

    /// POST METHOD
    public Student saveStudent(Student newStudent){
        return studentRepo.save(newStudent);
    }


    /// PUT METHOD
    public Student updateStudent(Long studentId, Student newStudent){
        return studentRepo.findById(studentId)
                .map(existingStudent -> {
                    existingStudent.setName(newStudent.getName());
                    existingStudent.setSurname(newStudent.getSurname());
                    existingStudent.setStudentNumber(newStudent.getStudentNumber());
                    return studentRepo.save(existingStudent);
                })
                .orElseThrow(() -> new EntityNotFoundException("Ogrenci bulunamadi")); // Eğer findById boş dönerse map çalışmaz ve null döner
    }

    public Boolean deleteStudent(Long studentId){
        if(studentRepo.existsById(studentId)){
            studentRepo.deleteById(studentId);
            return true;
        }
        else{
            return false;
        }
    }

}
