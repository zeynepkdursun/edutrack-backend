package com.admin.edu_track.requests;

import com.admin.edu_track.embeddings.Rankings;
import com.admin.edu_track.responses.LessonScoreDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExamResultRequestDto {
    private Long studentId; // Tüm öğrenci nesnesi yerine sadece ID
    private Long examId;

    private Double lgsScore;
    private Double netCount;
    private Integer totalCorrect;
    private Integer totalWrong;
    private List<LessonScoreDto> lessonScores;
    private Rankings rankings; // Embedded olduğu için doğrudan eklenebilir
}
