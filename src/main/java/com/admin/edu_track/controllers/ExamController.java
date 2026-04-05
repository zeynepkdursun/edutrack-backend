package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.Exam;
import com.admin.edu_track.repositories.AcademicYearRepository;
import com.admin.edu_track.requestDto.ExamRequestDto;
import com.admin.edu_track.services.ExamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
/*@CrossOrigin(
        origins = {
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        }
)*/
//@AllArgsConstructor
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    private final AcademicYearRepository yearRepo;

    @GetMapping()
    public ResponseEntity<List<Exam>> getAllExams(){
        return ResponseEntity.ok(examService.getAllExams());
    }


    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long examId){
        Exam exam = examService.getExamById(examId);
        if(exam == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exam);
    }

    // Yaklaşım (Spesifik): /search?yearId=1.
    // Eğer ileride buraya title veya level gibi 5-6 farklı filtre eklenecekse /search demek çok mantıklıdır.
    @GetMapping("/search") //buna ("/search") gerek var mi????
    public ResponseEntity<List<Exam>> searchExams(@RequestParam(name="yearId") Optional<Long> academicYearId,
                                                               @RequestParam(name="level") Optional<Integer> level){
        if(academicYearId.isPresent() && level.isPresent()){
            return ResponseEntity.ok(examService.getExamByAcademicYearIdAndLevel(academicYearId.get(), level.get()));
        }
        else if(academicYearId.isPresent()){
            return ResponseEntity.ok(examService.getExamByAcademicYearId(academicYearId.get()));
        }
        else if(level.isPresent()){
            return ResponseEntity.ok(examService.getExamByLevel(level.get()));
        }
        else {
            return ResponseEntity.ok(examService.getAllExams());
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody ExamRequestDto examDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.saveExam(examDto));
    }

    @PutMapping("/{examId}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long examId, @RequestBody Exam exam){
        Exam updatedExam = examService.updateExam(examId, exam);
        return ResponseEntity.ok(updatedExam);
    }
    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long examId){
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

}
