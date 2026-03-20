package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.services.AcademicYearService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/years")
public class AcademicYearController {
    private final AcademicYearService yearService;

    public AcademicYearController(AcademicYearService yearService){
        this.yearService = yearService;
    }

    @GetMapping
    public ResponseEntity<List<AcademicYear>> getAcademicYears(){
        return ResponseEntity.ok(yearService.getAllAcademicYears());
    }
    @GetMapping("/{id}")
    public ResponseEntity<AcademicYear> getAcademicYearById(@PathVariable Long id){
        AcademicYear academicYear = yearService.getAcademicYearById(id);
        if(academicYear != null){
            return ResponseEntity.ok(academicYear);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/search/active")
    public ResponseEntity<AcademicYear> getActiveAcademicYear(){
        return ResponseEntity.ok(yearService.getActiveAcademicYear());
    }
    @PostMapping
    public ResponseEntity<AcademicYear> createAcademicYear(@RequestBody AcademicYear year){
        return ResponseEntity.status(HttpStatus.CREATED).body(yearService.createAcademicYear(year));
    }


    @PutMapping("/change/{id}")
    public ResponseEntity<AcademicYear> changeActiveAcademicYear(@PathVariable("id") Long yearId){
        return ResponseEntity.ok(yearService.activateAcademicYear(yearId));
        //testidfdng to commit
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable Long id){
        if(yearService.deleteAcademicYear(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
