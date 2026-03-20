package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.responses.StudentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByStudentNumber(String studentNo);

/*    @Query("SELECT sr.student FROM StudentRegistry sr " +
            "WHERE (:yearId IS NULL OR sr.academicYear.id = :yearId) " +
            "AND (:classId IS NULL OR sr.schoolClass.id = :classId)")
    List<Student> search(@Param("yearId") Long yearId,
                         @Param("classId") Long classId);
*/
    @Query("SELECT new com.admin.edu_track.responses.StudentResponseDto(" +
            "s.id, s.name, s.surname, s.studentNumber, sc.level, sc.branch, ay.id, ay.label) " +
            "FROM Student s " +
            "JOIN StudentRegistry sr ON s.id = sr.student.id " +
            "JOIN SchoolClass sc ON sc.id = sr.schoolClass.id " +
            "JOIN AcademicYear ay ON ay.id = sr.academicYear.id "
            /*"WHERE ay.isActive = true"*/)
    List<StudentResponseDto> findAllStudents();




}
