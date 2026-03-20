package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    boolean existsByLevelAndBranchAndAcademicYearId(int level, String branch, long yearId);
    List<SchoolClass> findAllByLevel(int level);
    List<SchoolClass> findAllByAcademicYearId(long yearId);

}
