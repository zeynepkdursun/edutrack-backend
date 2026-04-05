package com.admin.edu_track.entities;

import com.admin.edu_track.embeddings.Rankings;
import com.admin.edu_track.embeddings.ScoreMetrics;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Embedded
    private ScoreMetrics scoreMetrics;

    @Embedded
    private Rankings rankings;

    /*Sorun: Spring Boot tarafında ExamResult nesnesini dönerken
    lessonScores listesini Eager Load etmen (veya @EntityGraph kullanman) gerekebilir.
    Aksi halde Hibernate "Lazy Loading" yaptığı için liste boş gelir.*/
    // CascadeType.ALL sayesinde ExamResult kaydedilince LessonScores da kaydedilir
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonScore> lessonScores = new ArrayList<>();

    // --- HELPER METHODS ---

    // Çift yönlü ilişkiyi korumak için (Service katmanını rahatlatır)
    public void addLessonScore(LessonScore score) {
        lessonScores.add(score);
        score.setExamResult(this);
    }

    public void removeLessonScore(LessonScore score) {
        lessonScores.remove(score);
        score.setExamResult(null);
    }

    // Hibernate/JPA için güvenli equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamResult that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
