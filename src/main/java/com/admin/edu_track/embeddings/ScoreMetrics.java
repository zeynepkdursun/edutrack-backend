package com.admin.edu_track.embeddings;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ScoreMetrics {
    private int correctCount;
    private int wrongCount;
    private double netCount;
    private double lgsScore;
}
