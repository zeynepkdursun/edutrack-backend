package com.admin.edu_track.services;


import com.admin.edu_track.entities.SchoolClass;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.exceptions.EntityNotFoundException;
import com.admin.edu_track.repositories.SchoolClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolClassService {
    private SchoolClassRepository classRepo;
    public SchoolClassService(SchoolClassRepository classRepo){
        this.classRepo = classRepo;
    }
    /// GET METHODS
    public List<SchoolClass> getAllClasses(){
        return classRepo.findAll();
    }
    public SchoolClass getClassById(Long classId){
        return classRepo.findById(classId).orElse(null);
    }
    public List<SchoolClass> getClassesByLevel(int level) {
        return classRepo.findAllByLevel(level);
    }
    public List<SchoolClass> getClassesByAcademicYearId(long yearId) {
        return classRepo.findAllByAcademicYearId(yearId);
    }

    ///  POST METHOD
    public SchoolClass createClass(SchoolClass newClass){
        // Kontrol: Bu level ve branch kombinasyonu zaten var mı?
        boolean exists = classRepo.existsByLevelAndBranchAndAcademicYearId(
                newClass.getLevel(),
                newClass.getBranch(),
                newClass.getAcademicYear().getId()
        );
        if (exists){
            throw new AlreadyExistsException("Bu sinif (Orn: " + newClass.getLevel() + "-" +
                    newClass.getBranch() + ") zaten sistemde kayitli!");
        }
        newClass.setBranch(newClass.getBranch().toUpperCase());
        return classRepo.save(newClass);
    }

    public SchoolClass updateClass(Long classId, SchoolClass newClass){
        // 1. Önce güncellenecek sınıfı bul
        SchoolClass existingClass = classRepo.findById(classId).orElseThrow(
                () -> new EntityNotFoundException("Sinif bulunamadi")
        );

        // Değişiklik kontrolü: Level, Branch VEYA Year ID değişmiş mi?
        boolean isChanged = existingClass.getLevel() != newClass.getLevel() ||
                existingClass.getBranch() != newClass.getBranch() ||
                !existingClass.getAcademicYear().getId().equals(newClass.getAcademicYear().getId());

        // 2. Eğer level veya branch değişmişse, yeni halinin başkasıyla çakışıp çakışmadığına bak
        if (isChanged) {
            boolean exists = classRepo.existsByLevelAndBranchAndAcademicYearId(
                    newClass.getLevel(),
                    newClass.getBranch(),
                    newClass.getAcademicYear().getId()
            );

            if (exists) {
                throw new AlreadyExistsException("Bu seviye ve şubede başka bir sınıf zaten tanımlı!");
            }
        }
        // 3. Güncelleme işlemini yap
        existingClass.setLevel(newClass.getLevel());
        existingClass.setBranch(newClass.getBranch());
        existingClass.setAcademicYear(newClass.getAcademicYear());
        return classRepo.save(existingClass);
    }



    ///  DELETE METHOD
    public void deleteClass(Long classId){
        SchoolClass schoolClass = classRepo.findById(classId).orElseThrow(() -> new EntityNotFoundException("Sinif bulunamadi"));
        classRepo.delete(schoolClass);
    }
}
