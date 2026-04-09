package com.admin.edu_track.mappers;

import com.admin.edu_track.entities.*;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.LessonScoreDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExamResultMapper {

    public ExamResult toEntity(ExamResultRequestDto examResultRequestDto, Student student, Exam exam, Map<String, Lesson> lessonMap){
        if (examResultRequestDto == null) return null;
        ExamResult examResult = new ExamResult();
        // ID'leri değil, Service'in veritabanından bulup getirdiği nesneleri set ediyoruz
        examResult.setStudent(student);
        examResult.setExam(exam);

        examResult.setScoreMetrics(examResultRequestDto.getScoreMetrics());
        examResult.setRankings(examResultRequestDto.getRankings());

        // 6. LessonScores Bağlantıları
        if (examResultRequestDto.getLessonScores() != null) {
            for (LessonScoreDto scoreDto : examResultRequestDto.getLessonScores()) {
                LessonScore scoreEntity = convertToLessonScoreEntity(scoreDto, lessonMap);
                examResult.addLessonScore(scoreEntity);
            }
        }
        return examResult;
    }

    public void updateEntityFromDto(ExamResultRequestDto examResultRequestDto, ExamResult existingExamResult){
        if(examResultRequestDto == null) {
            return;
        }
        if(examResultRequestDto.getScoreMetrics() != null){
            existingExamResult.setScoreMetrics(examResultRequestDto.getScoreMetrics());
        }
        if(examResultRequestDto.getRankings() != null){
            existingExamResult.setRankings(examResultRequestDto.getRankings());
        }
    }

    private LessonScore convertToLessonScoreEntity(LessonScoreDto lessonScoreDto, Map<String, Lesson> lessonMap) {
        Lesson lesson = lessonMap.get(lessonScoreDto.getLessonName());

        if(lesson == null) {
            throw new RuntimeException("Sistemde tanimli olmayan ders: " + scoreDto.getLessonName());
        }

        // her bir ders icin D/Y/Net'ler kaydediliyor (?)
        LessonScore score = new LessonScore();
        score.setLesson(lesson);
        // score.setExamResult(); Bu olmayacak mi???????
        score.setCorrectCount(scoreDto.getCorrectCount());
        score.setWrongCount(scoreDto.getWrongCount());
        score.setNetCount(scoreDto.getNetCount());
        return score;

    }
}
