package com.admin.edu_track.requestDto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ExamRequestDto {
    private String title;
    private int level;
    private LocalDate date;
    private Long academicYearId;
}
