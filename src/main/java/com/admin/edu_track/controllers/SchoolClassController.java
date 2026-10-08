package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.SchoolClass;
import com.admin.edu_track.services.SchoolClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    private final SchoolClassService classService;

    public SchoolClassController(SchoolClassService classService){
        this.classService = classService;
    }
    // http://localhost:8080/api/classes?level=7&yearId=1
    // GET /api/classes
    // GET /api/classes?yearId=5
    // GET /api/classes?level=8
    // GET /api/classes?yearId=5&level=8 (both are optional)
    @GetMapping
    public ResponseEntity<List<SchoolClass>> getAllSchoolClasses(
            @RequestParam(required = false) Long yearId,
            @RequestParam(required = false) Integer level){
        return ResponseEntity.ok(classService.getClasses(yearId, level));
    }

    @GetMapping("/{classId}")
    public ResponseEntity<SchoolClass> getSchoolClassById(@PathVariable Long classId){
        return ResponseEntity.ok(classService.getClassById(classId));
    }

    @PostMapping()
    public ResponseEntity<SchoolClass> createSchoolClass(@Valid @RequestBody SchoolClass newSchoolClass){
        SchoolClass createdClass = classService.createClass(newSchoolClass);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<SchoolClass> updateSchoolClass(@PathVariable Long classId, @Valid @RequestBody SchoolClass schoolClass){
        SchoolClass updated = classService.updateClass(classId, schoolClass);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteSchoolClass(@PathVariable Long classId){
        classService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

}
