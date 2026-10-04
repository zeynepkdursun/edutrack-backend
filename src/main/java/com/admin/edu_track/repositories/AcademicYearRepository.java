package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByIsActiveTrue(); // finds and returns the active year
    boolean existsByLabel(String label); // to check "if the year already exists" by searching for the label
    boolean existsByIsActiveTrue();
}
