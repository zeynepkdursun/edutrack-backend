package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.AcademicYear;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.entities.StudentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRegistryRepository extends JpaRepository<StudentRegistry, Long>{
    public List<StudentRegistry> findBySchoolClassId(Long classId);

    // JPQL (Java Persistence Query Language)
    // JPQL Java sınıflarıyla ve onların fieldlarıyla konusur SQL'den farklı olarak
    // yani, Java'daki Entity'lerini referans alınır
    // SQL talks with the db's tables and columns
    @Query("SELECT r.student FROM StudentRegistry r " +
            "WHERE r.schoolClass.id= :classId " +
            "AND r.academicYear.isActive = true") //sadece aktif yildakileri getir
    List<Student> findCurrentStudentsBySchoolClassId(@Param("classId") Long classId);

    @Query("SELECT r.student FROM StudentRegistry r " +
            "WHERE (:yearId IS NULL OR r.academicYear.id = :yearId) " +
            "AND (:level IS NULL OR r.schoolClass.level = :level) " +
            "AND (:studentNumber IS NULL OR r.student.studentNumber = :studentNumber) ")
    List<Student> searchStudentsForExam(
            @Param("yearId") Long yearId,
            @Param("level") Integer level,
            @Param("studentNumber") String studentNum
    );

    @Query("SELECT r.schoolClass.level FROM StudentRegistry r " +
            "WHERE r.student.id = :studentId " +
            "AND r.academicYear.id = :yearId")
    Optional<Integer> findSchoolClassLevelByStudentIdAndYearId(@Param("studentId") Long studentId, @Param("yearId") Long yearId);

    boolean existsByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);
    boolean existsByStudentIdAndAcademicYearIdAndIdNot(Long studentId, Long academicYearId, Long id);

    List<StudentRegistry> findAllByStudentIdOrderByAcademicYearLabelDesc(Long studentId);

    @Query("SELECT r.schoolClass.branch FROM StudentRegistry r " +
            "WHERE r.student.id = :studentId " +
            "AND r.academicYear.id = :yearId ")
    Optional<String> findSchoolClassBranchByStudentIdAndAcademicYearId(@Param("studentId") Long studentId, @Param("yearId") Long yearId);
}

/*
* id 1           -> id 1
* student cemre 1 -> cemre 1
* classId 8A 12    -> 8B 13
* yearId 2025 1   -> 2025 1

*
*
* */