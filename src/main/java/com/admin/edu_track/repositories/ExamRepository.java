package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>{

    List<Exam> findByAcademicYearId(Long academicYearId);
    List<Exam> findByLevel(int level);
    List<Exam> findByAcademicYearIdAndLevel(Long academicYearId, int level);

}
