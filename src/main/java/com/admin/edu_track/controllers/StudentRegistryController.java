package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.StudentRegistry;
import com.admin.edu_track.services.StudentRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registry")
public class StudentRegistryController {
    private StudentRegistryService registryService;
    public StudentRegistryController(StudentRegistryService registryService){
        this.registryService = registryService;
    }

    @GetMapping("/search/student/{studentId}")
    public ResponseEntity<List<StudentRegistry>> getAllRegistriesByStudentId(@PathVariable("studentId") Long id){
        List<StudentRegistry> registries = registryService.getRegistriesByStudentId(id);
        if (registries.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(registries);
    }
    @PostMapping
    public ResponseEntity<StudentRegistry> createRegistry(@RequestBody StudentRegistry registry){
        return ResponseEntity.status(HttpStatus.CREATED).body(registryService.createRegistry(registry));
    }

    @PutMapping("/{registryId}")
    public ResponseEntity<StudentRegistry> updateRegistry(@PathVariable Long registryId, @RequestBody StudentRegistry registry){
        return ResponseEntity.ok(registryService.updateRegistry(registryId, registry));
    }
    @DeleteMapping("/{registryId}")
    public ResponseEntity<Void> deleteRegistry(@PathVariable("registryId") Long id){
        if(registryService.deleteRegistry(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
