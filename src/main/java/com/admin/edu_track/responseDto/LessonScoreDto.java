package com.admin.edu_track.responseDto;

import com.admin.edu_track.embeddings.ScoreMetrics;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonScoreDto
{
    private String lessonName;
    @Embedded
    private ScoreMetrics scoreMetrics;
}