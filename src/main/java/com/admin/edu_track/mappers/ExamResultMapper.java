package com.admin.edu_track.mappers;

import com.admin.edu_track.embeddings.ScoreMetrics;
import com.admin.edu_track.entities.*;
import com.admin.edu_track.repositories.StudentRegistryRepository;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.ExamResultResponseDto;
import com.admin.edu_track.responseDto.LessonScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExamResultMapper {

    private final StudentRegistryRepository registryRepo;

    private static final ScoreMetrics DEFAULT_METRICS = new ScoreMetrics();

    public ExamResult toEntity(ExamResultRequestDto examResultRequestDto, Student student, Exam exam,
                               Map<String, Lesson> lessonMap, Map<String, LessonScoreDto> lessonScoreDtoMap) {
        if (examResultRequestDto == null) return null;

        ExamResult examResult = new ExamResult();
        examResult.setStudent(student);
        examResult.setExam(exam);
        examResult.setScoreMetrics(examResultRequestDto.getScoreMetrics());
        examResult.setRankings(examResultRequestDto.getRankings());

        for (Lesson lesson : lessonMap.values()) {
            LessonScore score = new LessonScore();
            score.setLesson(lesson);
            score.setExamResult(examResult);

            LessonScoreDto lessonScoreDto = lessonScoreDtoMap.get(lesson.getName());

            if (lessonScoreDto != null && lessonScoreDto.getScoreMetrics() != null) {
                score.setScoreMetrics(lessonScoreDto.getScoreMetrics());
            } else {
                score.setScoreMetrics(DEFAULT_METRICS);
            }
            examResult.addLessonScore(score);
        }
        return examResult;
    }

    public void updateEntityFromDto(ExamResultRequestDto examResultRequestDto, ExamResult existingExamResult) {
        if (examResultRequestDto == null) {
            return;
        }
        if (examResultRequestDto.getScoreMetrics() != null) {
            existingExamResult.setScoreMetrics(examResultRequestDto.getScoreMetrics());
        }
        if (examResultRequestDto.getRankings() != null) {
            existingExamResult.setRankings(examResultRequestDto.getRankings());
        }
    }

    public ExamResultResponseDto toResponseDto(ExamResult examResult) {
        if (examResult == null) {
            return null;
        }

        // 1. Şube bilgisini çek (Kayıt yoksa veya null ise projeyi kırmamak için fallback string veriyoruz)
        String branchAtThatTime = "";
        if (examResult.getStudent() != null && examResult.getExam() != null && examResult.getExam().getAcademicYear() != null) {
            branchAtThatTime = registryRepo.findSchoolClassBranchByStudentIdAndAcademicYearId(
                    examResult.getStudent().getId(),
                    examResult.getExam().getAcademicYear().getId()
            ).orElse("-");
        }

        // 2. ScoreMetrics doğrudan entity üzerinde veya embedded nesne içinde olabilir
        // Eğer examResult.getScoreMetrics() kullanılıyorsa oradan, doğrudan getter varsa oradan çeker:
        Double netCount = examResult.getScoreMetrics() != null ? examResult.getScoreMetrics().getNetCount() : 0.0;
        Integer correctCount = examResult.getScoreMetrics() != null ? examResult.getScoreMetrics().getCorrectCount() : 0;
        Integer wrongCount = examResult.getScoreMetrics() != null ? examResult.getScoreMetrics().getWrongCount() : 0;

        // 3. Response DTO oluştur
        ExamResultResponseDto dto = new ExamResultResponseDto(
                examResult.getId(),
                examResult.getStudent() != null ? examResult.getStudent().getName() : null,
                examResult.getStudent() != null ? examResult.getStudent().getSurname() : null,
                examResult.getStudent() != null ? examResult.getStudent().getStudentNumber() : null,
                branchAtThatTime,
                examResult.getExam() != null ? examResult.getExam().getLevel() : null,
                examResult.getExam() != null ? examResult.getExam().getTitle() : null,
                examResult.getExam() != null ? examResult.getExam().getDate() : null,
                examResult.getLgsScore(),
                netCount,
                correctCount,
                wrongCount,
                examResult.getRankings()
        );

        // 4. Ders skorlarını listeye çevir
        if (examResult.getLessonScores() != null) {
            List<LessonScoreDto> scoreDtos = examResult.getLessonScores().stream()
                    .map(ls -> {
                        String lessonName = (ls.getLesson() != null) ? ls.getLesson().getName() : "";
                        ScoreMetrics metrics = ls.getScoreMetrics() != null ? ls.getScoreMetrics() : DEFAULT_METRICS;

                        // LessonScoreDto constructor'ına göre: (lessonName, scoreMetrics) veya tekil alanlar
                        return new LessonScoreDto(lessonName, metrics);
                    })
                    .collect(Collectors.toList());
            dto.setLessonScores(scoreDtos);
        } else {
            dto.setLessonScores(new ArrayList<>());
        }

        return dto;
    }
}