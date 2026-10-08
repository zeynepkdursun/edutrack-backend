package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    boolean existsByLevelAndBranchAndAcademicYearId(int level, String branch, Long yearId);
    @Query("SELECT c FROM SchoolClass c " +
            "WHERE (:yearId IS NULL OR c.academicYear.id = :yearId) " +
            "AND (:level IS NULL OR c.level = :level)")
    List<SchoolClass> findClassesByFilter(@Param("yearId") Long yearId, @Param("level") Integer level);

}
