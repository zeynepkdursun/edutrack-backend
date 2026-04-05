package com.admin.edu_track.responseDto;

import com.admin.edu_track.embeddings.Rankings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data // Lombok ile getter/setter/constructor otomatik oluşur
@AllArgsConstructor
@NoArgsConstructor
public class ExamResultResponseDto {
    private Long id;
    private String studentName;
    private String studentSurname;
    private String studentNumber;
    private String branch;
    private int level;

    private String examTitle;
    private LocalDate date;

    private double lgsScore;
    private double netCount;
    private int totalCorrect;
    private int totalWrong;
    private Rankings rankings;
    // Bunu constructor içinde değil, sonradan Service'de dolduracağız
    private List<LessonScoreDto> lessonScores = new ArrayList<>();

    // Repository'deki "new" sorgusu için özel bir constructor (List hariç)
    public ExamResultResponseDto(Long id, String studentName, String studentSurname,
                                 String studentNumber, String branch, int level,
                                 String examTitle, LocalDate date,
                                 double lgsScore, double netCount, int totalCorrect,
                                 int totalWrong, Rankings rankings) {
        this.id = id;
        this.studentName = studentName;
        this.studentSurname = studentSurname;
        this.studentNumber = studentNumber;
        this.branch = branch;
        this.level = level;

        this.examTitle = examTitle;
        this.date = date;

        this.lgsScore = lgsScore;
        this.netCount = netCount;
        this.totalCorrect = totalCorrect;
        this.totalWrong = totalWrong;
        this.rankings = rankings;
    }
}