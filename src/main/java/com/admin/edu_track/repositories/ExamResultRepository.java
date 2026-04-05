package com.admin.edu_track.repositories;
import com.admin.edu_track.entities.ExamResult;
import com.admin.edu_track.responseDto.ExamResultResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long>{


    @Query("SELECT er FROM ExamResult er " +
            "WHERE (:studentId IS NULL OR er.student.id = :studentId) " +
            "AND (:examId IS NULL OR er.exam.id = :examId)")
    List<ExamResult> search(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Query("SELECT new com.admin.edu_track.responseDto.ExamResultResponseDto(" +
            "er.id, s.name, s.surname, s.studentNumber, reg.schoolClass.branch, reg.schoolClass.level, " +
            "er.exam.title, er.exam.date, er.lgsScore, er.netCount, er.correctCount, er.wrongCount, er.rankings) " +
            "FROM ExamResult er " +
            "JOIN er.student s " +
            "JOIN StudentRegistry reg ON reg.student.id = s.id AND reg.academicYear.id = er.exam.academicYear.id " +
            "WHERE (:studentId IS NULL OR er.student.id = :studentId) " +
            "AND (:examId IS NULL OR er.exam.id = :examId)")
    List<ExamResultResponseDto> searchDtos(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Query("SELECT DISTINCT er FROM ExamResult er " +
            "JOIN FETCH er.student s " +
            "JOIN FETCH er.exam e " +
            "JOIN FETCH e.academicYear ay " +
            "LEFT JOIN FETCH er.lessonScores ls " + // Ders skorlarını da yükle
            "WHERE (:studentId IS NULL OR s.id = :studentId) " +
            "AND (:examId IS NULL OR e.id = :examId)")
    List<ExamResult> findAllWithDetails(@Param("studentId") Long studentId, @Param("examId") Long examId);

    @Query("SELECT new com.admin.edu_track.responseDto.ExamResultResponseDto(" +
            "r.id, s.name, s.surname, s.studentNumber, reg.schoolClass.branch, reg.schoolClass.level, " +
            "r.exam.title, r.exam.date, r.lgsScore, r.netCount, r.correctCount, r.wrongCount, r.rankings) " +
            "FROM ExamResult r " +
            "JOIN r.student s " +
            "JOIN StudentRegistry reg ON reg.student.id = s.id AND reg.academicYear.id = r.exam.academicYear.id " +
            "WHERE r.exam.id = :examId")
    List<ExamResultResponseDto> findResultsByExamId(@Param("examId") Long examId);

    boolean existsByStudentIdAndExamId(Long studentId, Long examId);

}
