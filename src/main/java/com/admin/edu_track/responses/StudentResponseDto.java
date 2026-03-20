package com.admin.edu_track.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private long id;
    private String name;
    private String surname;
    private String studentNumber;
    private int currentLevel;
    private String currentBranch;
    private long academicYearId;
    private String academicYearLabel;

    public String getClassName(){
        return currentLevel + "-" + currentBranch;
    }

}



