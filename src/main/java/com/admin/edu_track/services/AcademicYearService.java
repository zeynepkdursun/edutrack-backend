package com.admin.edu_track.services;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.repositories.AcademicYearRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcademicYearService {
    private final AcademicYearRepository yearRepo;

    public AcademicYearService(AcademicYearRepository yearRepo){
        this.yearRepo = yearRepo;
    }
    ///  GET METHODS
    public List<AcademicYear> getAllAcademicYears(){
        return yearRepo.findAll();
    }
    public AcademicYear getAcademicYearById(Long yearId){
        return yearRepo.findById(yearId).orElse(null);
    }
    public AcademicYear getActiveAcademicYear(){
        return yearRepo.findByIsActiveTrue().get();
    }

    ///  POST METHODS
    public AcademicYear createAcademicYear(AcademicYear year){
        if(!AcademicYear.isSequentialYear(year.getLabel())){
            throw new IllegalArgumentException("Yıllar ardışık olmalıdır.");
        }

        if(yearRepo.existsByLabel(year.getLabel())){
            throw new AlreadyExistsException("Bu akademik yıl zaten kayıtlı!");
        }
        if(yearRepo.existsByIsActiveTrue() && year.isActive()){
            throw new AlreadyExistsException("Sistemde aktif akademik yıl zaten mevcut!");
        }
        return yearRepo.save(year);
    }

    @Transactional
    public AcademicYear activateAcademicYear(Long newYearId){
        yearRepo.findByIsActiveTrue().ifPresent(oldYear -> {
            oldYear.setActive(false);
            yearRepo.save(oldYear);
        });
        AcademicYear newYear = yearRepo.findById(newYearId)
                .orElseThrow(() -> new RuntimeException("Aktif edilecek yil bulunamadi!"));

        newYear.setActive(true);
        return yearRepo.save(newYear);
    }

    public boolean deleteAcademicYear(Long yearId){
        if (yearRepo.existsById(yearId)){
            yearRepo.deleteById(yearId);
            return true;
        }
        return false;
    }
}
