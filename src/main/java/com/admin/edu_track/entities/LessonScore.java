package com.admin.edu_track.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="lesson_score", indexes = {
        @Index(name = "idx_ls_exam_result_id", columnList = "exam_result_id")
})
@Data
public class LessonScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="exam_result_id", nullable = false)
    private ExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lesson_id", nullable = false)
    private Lesson lesson;

    private int correctCount;
    private int wrongCount;
    private double netCount;

}
