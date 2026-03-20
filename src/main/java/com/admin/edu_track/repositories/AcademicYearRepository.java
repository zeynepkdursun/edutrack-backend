package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByIsActiveTrue();
    boolean existsByLabel(String label);
    boolean existsByIsActiveTrue();
}
