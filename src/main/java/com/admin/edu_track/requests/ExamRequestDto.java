package com.admin.edu_track.requests;


import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ExamRequestDto {
    private String title;
    private int level;
    private LocalDate date;
    private Long academicYearId;
}
