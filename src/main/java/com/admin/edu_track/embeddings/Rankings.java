package com.admin.edu_track.embeddings;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rankings {
    private int classRank;
    private int schoolRank;
    private int districtRank;
    private int cityRank;
}
