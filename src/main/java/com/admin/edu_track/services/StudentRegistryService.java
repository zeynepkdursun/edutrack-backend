package com.admin.edu_track.services;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.entities.SchoolClass;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.entities.StudentRegistry;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.exceptions.EntityNotFoundException;
import com.admin.edu_track.repositories.AcademicYearRepository;
import com.admin.edu_track.repositories.SchoolClassRepository;
import com.admin.edu_track.repositories.StudentRegistryRepository;
import com.admin.edu_track.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentRegistryService {
    private final StudentRegistryRepository registryRepo;
    private final StudentRepository studentRepo;
    private final SchoolClassRepository classRepo;
    private final AcademicYearRepository yearRepo;
    public StudentRegistryService(StudentRegistryRepository registryRepo,
                                    StudentRepository studentRepo,
                                    SchoolClassRepository classRepo,
                                    AcademicYearRepository yearRepo){
        this.registryRepo = registryRepo;
        this.studentRepo = studentRepo;
        this.classRepo = classRepo;
        this.yearRepo = yearRepo;
    }

    public List<StudentRegistry> getRegistriesByStudentId(Long studentId){
        return registryRepo.findAllByStudentIdOrderByAcademicYearLabelDesc(studentId);
    }

    public StudentRegistry createRegistry(StudentRegistry newRegistry){

        if(registryRepo.existsByStudentIdAndAcademicYearId(
                newRegistry.getStudent().getId(),
                newRegistry.getAcademicYear().getId())) {
            throw new AlreadyExistsException("Bu öğrenci bu akademik yıl için zaten bir sınıfa kayıtlı!");
        }
            // 2. Veritabanından tam nesneleri çek ve parametre gelen nesneye set et
        // Bu sayede yeni bir 'new StudentRegistry()' oluşturmana gerek kalmaz.
        newRegistry.setStudent(studentRepo.findById(newRegistry.getStudent().getId())
                .orElseThrow(() -> new EntityNotFoundException("Öğrenci bulunamadı")));

        newRegistry.setSchoolClass(classRepo.findById(newRegistry.getSchoolClass().getId())
                .orElseThrow(() -> new EntityNotFoundException("Sınıf bulunamadı")));

        newRegistry.setAcademicYear(yearRepo.findById(newRegistry.getAcademicYear().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akademik yıl bulunamadı")));
        return registryRepo.save(newRegistry);
    }

    @Transactional
    public StudentRegistry updateRegistry(Long registryId, StudentRegistry newRegistry){
        if(registryRepo.existsByStudentIdAndAcademicYearIdAndIdNot(
                newRegistry.getStudent().getId(),
                newRegistry.getAcademicYear().getId(),
                registryId)) {
            throw new AlreadyExistsException("Bu öğrenci bu yıl zaten başka bir sınıfa kayıtlı!");
        }
        Student newStudent = studentRepo.findById(newRegistry.getStudent().getId())
                .orElseThrow(()-> new EntityNotFoundException("Öğrenci bulunamadı"));
        SchoolClass newClass = classRepo.findById(newRegistry.getSchoolClass().getId())
                .orElseThrow(() -> new EntityNotFoundException("Sınıf bulunamadı"));
        AcademicYear newYear = yearRepo.findById(newRegistry.getAcademicYear().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akademik yıl bulunamadı"));
        return registryRepo.findById(registryId).map((currentRegistry) -> {
            currentRegistry.setStudent(newStudent);
            currentRegistry.setSchoolClass(newClass);
            currentRegistry.setAcademicYear(newYear);
            return registryRepo.save(currentRegistry);
        }).orElseThrow(() -> new EntityNotFoundException("Kayıt bulunamadı!"));
    }

    public boolean deleteRegistry(Long registryId){
        if (registryRepo.existsById(registryId)){
            registryRepo.deleteById(registryId);
            return true;
        }
        return false;
    }


    /*
    * id
    * student +
    * class
    * year
    * */
}
