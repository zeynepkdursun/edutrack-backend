package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.responseDto.StudentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s " +
            "FROM Student s " +
            "JOIN StudentRegistry sr ON s.id = sr.student.id " +
            "WHERE (:yearId = sr.academicYear.id AND :studentNo = s.studentNumber)")
    Student findByStudentNumberAndYearId(@Param("studentNo") String studentNo, @Param("yearId") Long academicYearId);

    @Query("SELECT new com.admin.edu_track.responseDto.StudentResponseDto(" +
            "s.id, s.name, s.surname, s.studentNumber, sc.level, sc.branch, ay.id, ay.label) " +
            "FROM Student s " +
            "JOIN StudentRegistry sr ON s.id = sr.student.id " +
            "JOIN SchoolClass sc ON sc.id = sr.schoolClass.id " +
            "JOIN AcademicYear ay ON ay.id = sr.academicYear.id " +
            "WHERE (:activeOnly = false OR ay.isActive = true)")
    List<StudentResponseDto> findAllStudents(@Param("activeOnly") boolean activeOnly);
    // activeOnly == true --> fetches only the active ones
    // activeOnly == false --> must fetch all of them
}
