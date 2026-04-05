package com.admin.edu_track.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonScoreDto
{
    private String lessonName;
    private Integer correctCount;
    private Integer wrongCount;
    private Double netCount;
}