package com.admin.edu_track.services;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.exceptions.ResourceNotFoundException;
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
        return yearRepo.findByIsActiveTrue().orElseThrow(() -> new ResourceNotFoundException("There is no active academic year!"));
    }

    ///  POST METHODS
    public AcademicYear createAcademicYear(AcademicYear year){
        if (!isSequentialYear(year.getLabel())){
            throw new IllegalArgumentException("Years must be sequential");
        }

        if (yearRepo.existsByLabel(year.getLabel())){
            throw new AlreadyExistsException("This academic year already exists!");
        }

        if (yearRepo.existsByIsActiveTrue() && year.isActive()){
            throw new AlreadyExistsException("There is already active academic year in the system!");

        }

        return yearRepo.save(year);
    }

    @Transactional
    public AcademicYear activateAcademicYear(Long newYearId){
        yearRepo.findByIsActiveTrue().ifPresent(oldYear -> {
            oldYear.setActive(false);
        });
        AcademicYear newYear = yearRepo.findById(newYearId)
                .orElseThrow(() -> new RuntimeException("No academic year found to activate!"));

        newYear.setActive(true);
        return newYear; // or return yearRepo.save(newYear);
    }

    public boolean deleteAcademicYear(Long yearId){
        if (yearRepo.existsById(yearId)){
            yearRepo.deleteById(yearId);
            return true;
        }
        return false;
    }

    private boolean isSequentialYear(String academicYear) {

        String[] years = academicYear.split("-");

        int first = Integer.parseInt(years[0]);
        int second = Integer.parseInt(years[1]);

        return second == first + 1;
    }
}
