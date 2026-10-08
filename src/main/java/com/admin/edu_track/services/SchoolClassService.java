package com.admin.edu_track.services;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.entities.SchoolClass;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.exceptions.ResourceNotFoundException;
import com.admin.edu_track.repositories.AcademicYearRepository;
import com.admin.edu_track.repositories.SchoolClassRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolClassService {
    private final SchoolClassRepository classRepo;
    private final AcademicYearRepository yearRepo;
    public SchoolClassService(SchoolClassRepository classRepo, AcademicYearRepository yearRepo){
        this.classRepo = classRepo;
        this.yearRepo = yearRepo;
    }
    /// GET METHODS
    public List<SchoolClass> getClasses(Long yearId, Integer level){
        return classRepo.findClassesByFilter(yearId, level);
    }
    public SchoolClass getClassById(Long classId){
        return classRepo.findById(classId).orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
    }

    ///  POST METHOD
    @Transactional
    public SchoolClass createClass(SchoolClass newClass){

        // 1. confirm if academicYear exists
        Long yearId = validateAndGetYearId(newClass);
        AcademicYear year = yearRepo.findById(yearId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + yearId));

        String branchUpper = newClass.getBranch().trim().toUpperCase();

        // Control: If this level/branch pair already exists?
        boolean exists = classRepo.existsByLevelAndBranchAndAcademicYearId(
                newClass.getLevel(),
                branchUpper,
                yearId
        );
        if (exists){
            throw new AlreadyExistsException("Class (" + newClass.getLevel() + "-" +
                    newClass.getBranch() + ") already exists in this academic year!");
        }
        newClass.setBranch(branchUpper);
        newClass.setAcademicYear(year);
        return classRepo.save(newClass);
    }

    @Transactional
    public SchoolClass updateClass(Long classId, SchoolClass newClass) {

        // 1. Get the old class from db
        SchoolClass existingClass = classRepo.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));


        // 2. New class values
        Long newYearId = validateAndGetYearId(newClass);
        String newBranch = newClass.getBranch().trim().toUpperCase();
        int newLevel = newClass.getLevel();

        // 3. Compare with the old one. Has level, branch or year really changed?
        boolean isLevelChanged = existingClass.getLevel() != newLevel;
        boolean isBranchChanged = !existingClass.getBranch().equalsIgnoreCase(newBranch);
        boolean isYearChanged = !existingClass.getAcademicYear().getId().equals(newYearId);

        // 4. If any field of three changed, control that if that class already exists.
        if (isLevelChanged || isBranchChanged || isYearChanged) {
            boolean exists = classRepo.existsByLevelAndBranchAndAcademicYearId(
                    newLevel,
                    newBranch,
                    newYearId
            );

            if (exists) {
                throw new AlreadyExistsException(
                        "Class " + newLevel + "-" + newBranch + " already exists in the target academic year!"
                );
            }
        }

        // 5. If year has changed, confirm the year and assign it
        if (isYearChanged) {
            AcademicYear newYear = yearRepo.findById(newYearId)
                    .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + newYearId));
            existingClass.setAcademicYear(newYear);
        }

        // 6. update the other fields and save

        existingClass.setLevel(newLevel);
        existingClass.setBranch(newBranch);

        return classRepo.save(existingClass);
    }



    ///  DELETE METHOD
    public void deleteClass(Long classId){
        SchoolClass schoolClass = classRepo.findById(classId).orElseThrow(() -> new ResourceNotFoundException("Class not found!"));
        classRepo.delete(schoolClass);
    }


    private Long validateAndGetYearId(SchoolClass schoolClass) {
        if (schoolClass.getAcademicYear() == null || schoolClass.getAcademicYear().getId() == null) {
            throw new IllegalArgumentException("Academic year ID must be provided!");
        }
        return schoolClass.getAcademicYear().getId();
    }
}
