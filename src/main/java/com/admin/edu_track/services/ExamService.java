package com.admin.edu_track.services;


import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.entities.Exam;
import com.admin.edu_track.repositories.AcademicYearRepository;
import com.admin.edu_track.repositories.ExamRepository;
import com.admin.edu_track.requests.ExamRequestDto;
import com.admin.edu_track.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {
    private final ExamRepository examRepo;
    private final AcademicYearRepository yearRepo;

    ///  GET METHODS
    public List<Exam> getAllExams(){
        return examRepo.findAll();
    }
    public Exam getExamById(Long examId){
        return examRepo.findById(examId).orElse(null);
    }
    public List<Exam> getExamByLevel(int level) {
        return examRepo.findByLevel(level);
    }
    public List<Exam> getExamByAcademicYearId(Long yearId){
        return examRepo.findByAcademicYearId(yearId);
    }
    public List<Exam> getExamByAcademicYearIdAndLevel(Long yearId, int level){
        return examRepo.findByAcademicYearIdAndLevel(yearId, level);
    }

    // sinavin yer aldigi akademic yilla date uyumlu olmalı!!!!!!!!!!!!!(todo later)
    ///  POST METHOD
    ///

    public Exam saveExam(ExamRequestDto dto) {
        Exam newExam = new Exam();
        newExam.setLevel(dto.getLevel());
        newExam.setTitle(dto.getTitle());
        newExam.setDate(dto.getDate());

        // Entity ilişkisini burada kuruyoruz
        newExam.setAcademicYear(yearRepo.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new EntityNotFoundException("Akademik yil bulunamadi")));

        return examRepo.save(newExam);
    }

    /// PUT METHOD
    public Exam updateExam(Long examId, Exam updatedExam){

        AcademicYear newYear = yearRepo.findById(updatedExam.getAcademicYear().getId()).orElseThrow(
                () -> new EntityNotFoundException("Akademik yil bulunamadi"));
        Exam existingExam = examRepo.findById(examId).orElseThrow(() -> new EntityNotFoundException("Sinav bulunamadi"));
        existingExam.setDate(updatedExam.getDate());
        existingExam.setTitle(updatedExam.getTitle());
        existingExam.setAcademicYear(newYear);
        existingExam.setLevel(updatedExam.getLevel());
        return examRepo.save(existingExam);
    }

    /// DELETE METHOD
    public void deleteExam(Long examId){
        Exam exam = examRepo.findById(examId).orElseThrow(() -> new EntityNotFoundException("Sinav bulunamadi"));
        examRepo.delete(exam);
    }
}
