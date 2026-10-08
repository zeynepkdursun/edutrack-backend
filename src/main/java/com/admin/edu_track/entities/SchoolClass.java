package com.admin.edu_track.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name="school_class",
        uniqueConstraints = {
        // In the same year, level/branch pairs must be unique
            @UniqueConstraint(
                    name = "unique_level_branch_year",
                    columnNames = {"level", "branch", "academic_year_id"}
            )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 5, message = "Grade level must be at least 5")
    @Max(value = 8, message = "Grade level cannot exceed 8")
    @Column(nullable = false)
    private int level;

    @NotBlank(message = "Branch is required")
    @Pattern(regexp = "^[A-Z]$", message = "Branch must be a single uppercase letter (e.g. A, B, C)")
    @Column(length = 1, nullable = false)
    private String branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    /*@ManyToOne
    @JoinColumn(name="school_id", nullable=true)
    private School school;*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolClass that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }



}
