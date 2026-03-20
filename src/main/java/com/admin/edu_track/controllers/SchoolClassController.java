package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.SchoolClass;
import com.admin.edu_track.services.SchoolClassService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes") //burada schoolClasses veya school-classes yerine sadece classes yazmam dogru mu
public class SchoolClassController {

    private final SchoolClassService classService;
    public SchoolClassController(SchoolClassService classService){
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<SchoolClass>> getAllSchoolClasses(){
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{classId}")
    public ResponseEntity<SchoolClass> getSchoolClassById(@PathVariable Long classId){
        SchoolClass schoolClass = classService.getClassById(classId);
        if(schoolClass != null){
            return ResponseEntity.ok(schoolClass);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/level/{level}")
    public ResponseEntity<List<SchoolClass>> getSchoolClassById(@PathVariable int level){
        return ResponseEntity.ok(classService.getClassesByLevel(level));
    }
    @GetMapping("/search")
    public ResponseEntity<List<SchoolClass>> searchClasses(@RequestParam(name="yearId") long yearId) {
        return ResponseEntity.ok(classService.getClassesByAcademicYearId(yearId));
    }

    /*@GetMapping("/search")
    //http://localhost:8080/api/classes/search?level=7&yearId=1
    public ResponseEntity<List<SchoolClass>> getSchoolClassByLevelAndAcademicYearId(@RequestParam int level, @RequestParam Long yearId){
        return ResponseEntity.ok(classService.getClassesByLevelAndYearId(level, yearId));
    }*/

    @PostMapping()
    public ResponseEntity<?> createSchoolClass(@RequestBody SchoolClass newSchoolClass){
        SchoolClass schoolClass = classService.createClass(newSchoolClass);
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolClass);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<?> updateSchoolClass(@PathVariable Long classId, @RequestBody SchoolClass schoolClass){
        SchoolClass updated = classService.updateClass(classId, schoolClass);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteSchoolClass(@PathVariable Long classId){
        classService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

}
