package com.admin.edu_track.entities;

import com.admin.edu_track.embeddings.Rankings;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_result", indexes = {
        @Index(name = "idx_perf_student_exam", columnList = "student_id, exam_id", unique = true)
})
@Data
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    private int correctCount;
    private int wrongCount;
    private double netCount;
    private double lgsScore;

    @Embedded
    private Rankings rankings;

    /*Sorun: Spring Boot tarafında ExamResult nesnesini dönerken
    lessonScores listesini Eager Load etmen (veya @EntityGraph kullanman) gerekebilir.
    Aksi halde Hibernate "Lazy Loading" yaptığı için liste boş gelir.*/
    // CascadeType.ALL sayesinde ExamResult kaydedilince LessonScores da kaydedilir
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonScore> lessonScores = new ArrayList<>();

}
