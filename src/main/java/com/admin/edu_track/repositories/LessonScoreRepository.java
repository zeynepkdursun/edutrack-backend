package com.admin.edu_track.repositories;

import com.admin.edu_track.entities.LessonScore;
import com.admin.edu_track.responses.LessonScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LessonScoreRepository extends JpaRepository<LessonScore, Long> {

    @Query("SELECT new com.admin.edu_track.responses.LessonScoreDto(" +
            "l.name, ls.correctCount, ls.wrongCount, ls.netCount) " +
            "FROM LessonScore ls " +
            "JOIN ls.lesson l " + // LessonScore içindeki lesson alanını kullanıyoruz
            "WHERE ls.examResult.id = :resultId")
    List<LessonScoreDto> findScoresByResultId(@Param("resultId") Long resultId);
}
