package com.admin.edu_track.services;


import com.admin.edu_track.constants.ErrorMessages;
import com.admin.edu_track.entities.*;
import com.admin.edu_track.exceptions.AlreadyExistsException;
import com.admin.edu_track.exceptions.ResourceNotFoundException;
import com.admin.edu_track.mappers.ExamResultMapper;
import com.admin.edu_track.repositories.*;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.ExamResultResponseDto;
import com.admin.edu_track.responseDto.LessonScoreDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final ExamResultRepository resultRepo;
    private final StudentRegistryRepository registryRepo;
    private final StudentRepository studentRepo;
    private final ExamRepository examRepo;
    private final LessonScoreRepository lessonScoreRepo;
    private final LessonRepository lessonRepo;
    private final ExamResultMapper resultMapper;

    /*public List<ExamResultResponseDto> getAllExamResultsDto(Long studentId, Long examId) {
        // 1. Veritabanından her şeyi tek sorguda çek (N+1 bitti!)
        List<ExamResult> results = resultRepo.findAllWithDetails(studentId, examId);

        // 2. Stream API ile her bir Entity'yi DTO'ya dönüştür
        return results.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }*/
    public List<ExamResultResponseDto> getAllExamResultsDto(Long studentId, Long examId) {
        List<ExamResult> results = resultRepo.findAllWithDetails(studentId, examId);
        return results.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    private ExamResultResponseDto convertToResponseDto(ExamResult examResult) {
        if (examResult == null) {
            return null;
        }

        ExamResultResponseDto dto = new ExamResultResponseDto();
        dto.setId(examResult.getId());

        // Öğrenci & Sınav Bilgileri
        if (examResult.getStudent() != null) {
            dto.setId(examResult.getStudent().getId());
            dto.setStudentName(examResult.getStudent().getName());
            dto.setStudentNumber(examResult.getStudent().getStudentNumber());
        }

        if (examResult.getExam() != null) {
            dto.setExamTitle(examResult.getExam().getTitle());
        }

        // Ders Notları / Skor Listesi
        if (examResult.getLessonScores() != null) {
            List<LessonScoreDto> scoreDtos = examResult.getLessonScores().stream()
                    .map(score -> {
                        LessonScoreDto scoreDto = new LessonScoreDto();
                        if (score.getLesson() != null) {
                            scoreDto.setLessonName(score.getLesson().getName());
                        }
                        scoreDto.setScoreMetrics(score.getScoreMetrics());
                        return scoreDto;
                    })
                    .collect(Collectors.toList());

            dto.setLessonScores(scoreDtos);
        }

        // Varsa toplam puan / net vb. alanlar:
        // dto.setTotalScore(examResult.getTotalScore());

        return dto;
    }

    @Transactional
    public ExamResult saveExamResult(ExamResultRequestDto examResultRequestDto){
        // 1. Once nesneler cekiliyor
        Student student = findStudentByIdOrThrow(examResultRequestDto.getStudentId());
        Exam exam = findExamByIdOrThrow(examResultRequestDto.getExamId());

        validateExamConsistency(student, exam, examResultRequestDto);

        // Dersleri Map'e Al (Performans ve Güvenlik İçin) (Performans için tek sorgu)
        // LessonMap: {Turkce, Lesson Object}
        Map<String, Lesson> lessonMap = fetchLessonMap();

        // lessonScoreDtoMap: { Turkce, LessonScoreDto(Turkce, scoreMetrics) }
        Map<String, LessonScoreDto> lessonScoreDtoMap = toLessonScoreDtoMap(examResultRequestDto.getLessonScores());

        // 4. MAPPING
        ExamResult examResult = resultMapper.toEntity(examResultRequestDto, student, exam, lessonMap, lessonScoreDtoMap);

        return resultRepo.save(examResult);
    }

    @Transactional
    public ExamResult saveExamResultWithNo(ExamResultRequestDto examResultRequestDto, String studentNo) {
        // 1. Numaradan öğrenciyi bul
        Student student = findStudentByStudentNumber(studentNo);
        if (student == null) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_NOT_FOUND_BY_NUMBER, studentNo));
        }
        // 2. Mevcut DTO'nun içine bulduğumuz ID'yi yerleştir
        examResultRequestDto.setStudentId(student.getId());
        // 3. Zaten yazdığın ve tüm kontrollerin olduğu asıl metodu çağır
        return saveExamResult(examResultRequestDto);
    }

    public ExamResultResponseDto updateExamResult(Long id, ExamResultRequestDto requestDto){
        // 1. Mevcut kaydı çek
        ExamResult existingResult = findExamResultByIdOrThrow(id);
        resultMapper.updateEntityFromDto(requestDto, existingResult);

        // 4. LessonScores (Tüm listeyi yenilemek en temizidir)
        if (requestDto.getLessonScores() != null) {
            // 1. Adım: Tüm dersleri tek bir sorguyla çek ve Map'e dönüştür
            Map<String, Lesson> lessonMap = getLessonMap(requestDto.getLessonScores());

            // 2. Mevcut skorları temizle (Yetim kayıt bırakmamak için)
            existingResult.getLessonScores().clear();

            // DTO'daki ders puanlarını Entity'ye çevirip ekle
            for (LessonScoreDto scoreDto : requestDto.getLessonScores()) {
                LessonScore newScore = new LessonScore();

                // 3. Adım: Veritabanına gitmek yerine Map'ten (hafızadan) getir
                Lesson lesson = lessonMap.get(scoreDto.getLessonName());

                if (lesson == null) {
                    throw new RuntimeException("Sistemde kayıtlı olmayan ders: " + scoreDto.getLessonName());
                }
                // 4. Bağlantıları kur (Çift yönlü ilişki)
                newScore.setLesson(lesson); // Hangi ders? -> Matematik
                newScore.setExamResult(existingResult); // Hangi sınav sonucu? -> Ahmet'in Denemesi

                // 5. Diğer verileri set et
                newScore.setScoreMetrics(scoreDto.getScoreMetrics());

                // 5. Ana listeye ekle
                existingResult.getLessonScores().add(newScore);
            }
        }
        ExamResult updated = resultRepo.save(existingResult);

        return convertToResponseDto(updated);
    }


    //Şu an yaptığımız şey aslında manuel bir Mapping (Eşleştirme) işlemi.
    // Projen büyüdüğünde her servis için bu kadar uzun kodlar yazmak yerine
    // MapStruct kütüphanesini kullanabilirsin.
    // MapStruct, senin verdiğin kurallara göre Entity -> DTO veya
    // DTO -> Entity dönüşümlerini derleme zamanında otomatik olarak kodlar.


    private void validateExamResult(ExamResult result){
        Long studentId = result.getStudent().getId();
        Long yearId = result.getExam().getAcademicYear().getId();

        // Kontrol: Bu öğrenci, bu sınavın yapıldığı yılda gerçekten okula kayıtlı mı?
        //student_id ---> student ---> registry ----> year
        //exam_id ----> exam ----> year
        boolean isRegisteredThatYear = registryRepo.existsByStudentIdAndAcademicYearId(studentId, yearId);
        if (!isRegisteredThatYear){
            throw new RuntimeException("Sinav sonucu kaydedilen ogrenci girilen akademik yilda kayitli degil");
        }

    }

    public boolean deleteExamResult(Long id){
        if(resultRepo.existsById(id)){
            resultRepo.deleteById(id);
            return true;
        }
        else{
            return false;
        }
    }


    //UPDATE KISMINI YAP SONRA DA LGSSCORE'LARINI GUNCELLE
    //OGRENCININ SINIFIYLA DENEMENIN LEVELININ (SINIFINI) AYNI OLMASI CONSTRAINTINI YAZ


    private Student findStudentByIdOrThrow(Long studentId) {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> {
                    String errorMessage = String.format(ErrorMessages.STUDENT_NOT_FOUND, studentId);
                    return new ResourceNotFoundException(errorMessage);
                });
    }
    private Student findStudentByStudentNumber(String studentNum){
        return studentRepo.findByStudentNumber(studentNum);
    }

    private Exam findExamByIdOrThrow(Long examId) {
        return examRepo.findById(examId)
                .orElseThrow(() -> {
                    String errorMessage = String.format(ErrorMessages.EXAM_NOT_FOUND, examId);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    private ExamResult findExamResultByIdOrThrow(Long examResultId){
        return resultRepo.findById(examResultId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.EXAM_RESULT_NOT_FOUND, examResultId)));
    }

    /*
        { "Math" -> Lesson(Math),
        "Physics" -> Lesson(Physics)}
    */
    // DB'den tum dersleri ceker
    private Map<String, Lesson> fetchLessonMap() {
        return lessonRepo.findAll().stream()
                .collect(Collectors.toMap(Lesson::getName, lesson -> lesson));
    }
    // DB'den sadece verilen dersleri ceker
    private Map<String, Lesson> getLessonMap(List<LessonScoreDto> lessonScores) {
        List<String> lessonNames = lessonScores.stream()
                .map(LessonScoreDto::getLessonName)
                .toList();
        return lessonRepo.findByNameInIgnoreCase(lessonNames).stream()
                .collect(Collectors.toMap(Lesson::getName, lesson -> lesson));
    }

    private Map<String, LessonScoreDto> toLessonScoreDtoMap(List<LessonScoreDto> list) {
        if (list == null) return Map.of();
        // lessonScoreDtoMap: { Turkce, LessonScoreDto(Turkce, scoreMetrics) }
        return list.stream()
                .collect(Collectors.toMap(
                        LessonScoreDto::getLessonName,
                        dto -> dto
                ));
    }
    private void validateExamConsistency(Student student, Exam exam, ExamResultRequestDto examResultRequestDto){
        // 2. Önce bu öğrenci bu sınava zaten girmiş mi? (Double Entry Check)
        // Çift kayıt kontrolü
        if (resultRepo.existsByStudentIdAndExamId(student.getId(), exam.getId())) {
            throw new AlreadyExistsException(
                    String.format(ErrorMessages.STUDENT_EXAM_RESULT_ALREADY_EXISTS, student.getId(), exam.getId())
            );
        }
        /// ///////////////////// , BİR ONCEKİ PROMPTTAKİ İKİ KULLANIM FARKINI OGREN
        //////////////////////////  BU İKİ KULLANIMI DA REFACTOR ET
        // Öğrencinin o yıldaki seviyesini (level) buluyoruz
        int studentLevel = registryRepo.findSchoolClassLevelByStudentIdAndYearId(examResultRequestDto.getStudentId(), exam.getAcademicYear().getId())
                .orElseThrow(() -> new RuntimeException("Öğrencinin bu yıl için kayıtlı bir sınıfı bulunamadı!"));

        // Kontrol: Bu öğrencinin seviyesi (sinifi: 5, 6, 7 veya 8),
        // bu sınavın seviyesiyle ayni mi?
        if (exam.getLevel() != studentLevel){
            throw new RuntimeException("HATA: Öğrencinin seviyesi (" + studentLevel + ") sınav seviyesiyle (" + exam.getLevel() + ") uyuşmuyor!"); }



    }
}
