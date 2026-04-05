package com.admin.edu_track.requestDto;

import com.admin.edu_track.embeddings.Rankings;
import com.admin.edu_track.embeddings.ScoreMetrics;
import com.admin.edu_track.responseDto.LessonScoreDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExamResultRequestDto {
    @NotNull(message = "Ogrenci ID zorunludur")
    private Long studentId; // Tüm öğrenci nesnesi yerine sadece ID
    @NotNull(message = "Sinav ID zorunludur")
    private Long examId;
    @Valid // ScoreMetrics içindeki validasyonların (varsa) çalışması için şart
    private ScoreMetrics scoreMetrics;
    @NotEmpty(message = "En az bir ders sonucu girilmelidir")
    private List<LessonScoreDto> lessonScores;
    private Rankings rankings; // Embedded olduğu için doğrudan eklenebilir
}
