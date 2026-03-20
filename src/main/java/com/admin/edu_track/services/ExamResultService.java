package com.admin.edu_track.services;


import com.admin.edu_track.entities.*;
import com.admin.edu_track.repositories.*;
import com.admin.edu_track.requests.ExamResultRequestDto;
import com.admin.edu_track.responses.ExamResultResponseDto;
import com.admin.edu_track.responses.LessonScoreDto;
import jakarta.persistence.EntityNotFoundException;
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

    public List<ExamResultResponseDto> getAllExamResultsDto(Long studentId, Long examId) {
        // 1. Veritabanından her şeyi tek sorguda çek (N+1 bitti!)
        List<ExamResult> results = resultRepo.findAllWithDetails(studentId, examId);

        // 2. Stream API ile her bir Entity'yi DTO'ya dönüştür
        return results.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        /*
        .map(this::convertToResponseDto)
        .map(er -> convertToResponseDto(er))
        */
    }

    public ExamResult saveExamResult(ExamResultRequestDto dto){
        // 1. Önce nesneleri güvenle çekiyoruz (findById ile NPE riskini sıfırlıyoruz)
        Student student = studentRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı"));
        Exam exam = examRepo.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("Sınav bulunamadı"));

        // 2. Önce bu öğrenci bu sınava zaten girmiş mi? (Double Entry Check)
        // Çift kayıt kontrolü
        if (resultRepo.existsByStudentIdAndExamId(student.getId(), exam.getId())) {
            throw new RuntimeException("Bu öğrenci için bu sınav sonucu zaten girilmiş!");
        }

        // 3. Akademik Kayıt ve Seviye Kontrolü
        // Kontrol: Bu öğrenci, bu sınavın yapıldığı yılda gerçekten okula kayıtlı mı?
        /*boolean isRegisteredThatYear = registryRepo.existsByStudentIdAndAcademicYearId(studentId, yearId);
        if (!isRegisteredThatYear){
            throw new RuntimeException("Sinav sonucu kaydedilen ogrenci girilen akademik yilda kayitli degil");
        }*/


        // Öğrencinin o yıldaki seviyesini (level) buluyoruz
        int studentLevel = registryRepo.findSchoolClassLevelByStudentIdAndYearId(dto.getStudentId(), exam.getAcademicYear().getId())
                .orElseThrow(() -> new RuntimeException("Öğrencinin bu yıl için kayıtlı bir sınıfı bulunamadı!"));

        // Kontrol: Bu öğrencinin seviyesi (sinifi: 5, 6, 7 veya 8),
        // bu sınavın seviyesiyle ayni mi?
        if (exam.getLevel() != studentLevel){
            throw new RuntimeException("HATA: Öğrencinin seviyesi (" + studentLevel + ") sınav seviyesiyle (" + exam.getLevel() + ") uyuşmuyor!"); }


        // 4. Entity Hazırlığı
        ExamResult examResult = new ExamResult();
        examResult.setStudent(student);
        examResult.setExam(exam);
        examResult.setCorrectCount(dto.getTotalCorrect());
        examResult.setWrongCount(dto.getTotalWrong());
        examResult.setNetCount(dto.getNetCount());
        examResult.setLgsScore(dto.getLgsScore());
        examResult.setRankings(dto.getRankings());


        // 5. Dersleri Map'e Al (Performans ve Güvenlik İçin)
        Map<String, Lesson> lessonMap = lessonRepo.findAll().stream()
                .collect(Collectors.toMap(Lesson::getName, lesson -> lesson));

        // 6. LessonScores Bağlantıları
        if (dto.getLessonScores() != null) {
            for (LessonScoreDto scoreDto : dto.getLessonScores()) {
                LessonScore score = new LessonScore();

                // Map üzerinden Lesson nesnesini bul
                Lesson lesson = lessonMap.get(scoreDto.getLessonName());
                if (lesson == null) {
                    throw new RuntimeException("Sistemde tanımlı olmayan ders: " + scoreDto.getLessonName());
                }

                score.setLesson(lesson);
                score.setCorrectCount(scoreDto.getCorrectCount());
                score.setWrongCount(scoreDto.getWrongCount());
                score.setNetCount(scoreDto.getNetCount());

                // Çift yönlü ilişkiyi kur (ExamResult <-> LessonScore)
                score.setExamResult(examResult);
                examResult.getLessonScores().add(score);
            }
        }
        return resultRepo.save(examResult);
    }

    public ExamResult saveExamResultWithNo(ExamResultRequestDto dto, String studentNo) {
        // 1. Numaradan öğrenciyi bul
        Student student = studentRepo.findByStudentNumber(studentNo);
        if (student == null) {
            throw new RuntimeException("Öğrenci numarası bulunamadı: " + studentNo);
        }

        // 2. Mevcut DTO'nun içine bulduğumuz ID'yi yerleştir
        dto.setStudentId(student.getId());

        // 3. Zaten yazdığın ve tüm kontrollerin olduğu asıl metodu çağır
        return saveExamResult(dto);
    }

    public ExamResultResponseDto updateExamResult(Long id, ExamResultRequestDto requestDto){
        // 1. Mevcut kaydı çek
        ExamResult existingResult = resultRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sonuc bulunamadi"));

        // 2. Sayısal verileri güncelle (Null kontrolü yaparak)
        if (requestDto.getLgsScore() != null) existingResult.setLgsScore(requestDto.getLgsScore());
        if (requestDto.getNetCount() != null) existingResult.setNetCount(requestDto.getNetCount());
        if (requestDto.getTotalCorrect() != null) existingResult.setCorrectCount(requestDto.getTotalCorrect());
        if (requestDto.getTotalWrong() != null) existingResult.setWrongCount(requestDto.getTotalWrong());

        // 3. Rankings güncelleme (Nested object kontrolü)
        if (requestDto.getRankings() != null) {
            existingResult.setRankings(requestDto.getRankings());
        }

        // 4. LessonScores (Tüm listeyi yenilemek en temizidir)
        if (requestDto.getLessonScores() != null) {
            // 1. Adım: Tüm dersleri tek bir sorguyla çek ve Map'e dönüştür
            // Veritabanına sadece 1 kez gidilir.
            Map<String, Lesson> lessonMap = lessonRepo.findAll().stream()
                    .collect(Collectors.toMap(Lesson::getName, lesson -> lesson));
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
                newScore.setCorrectCount(scoreDto.getCorrectCount());
                newScore.setWrongCount(scoreDto.getWrongCount());
                newScore.setNetCount(scoreDto.getNetCount());

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
    private ExamResultResponseDto convertToResponseDto(ExamResult entity) {
        // 1. Öğrencinin O SINAVIN AKADEMİK YILINDAKİ kaydını bul (JOIN mantığı kodda)
        // StudentRegistryRepository kullanarak o yılın şubesini çekiyoruz
        String branchAtThatTime = registryRepo.findSchoolClassBranchByStudentIdAndAcademicYearId(
                entity.getStudent().getId(),
                entity.getExam().getAcademicYear().getId()
        ).orElseThrow(() -> new EntityNotFoundException(
                "Öğrenci (ID: " + entity.getStudent().getId() +
                        ") bu sınavın yapıldığı yılda bir sınıfa kayıtlı değil!"));
// 2. DTO nesnesini constructor ile oluştur (Ders listesi hariç olan constructor)
        ExamResultResponseDto dto = new ExamResultResponseDto(
                entity.getId(),
                entity.getStudent().getName(),
                entity.getStudent().getSurname(),
                entity.getStudent().getStudentNumber(),
                branchAtThatTime,
                entity.getExam().getLevel(),
                entity.getExam().getTitle(),
                entity.getExam().getDate(),
                entity.getLgsScore(),
                entity.getNetCount(),
                entity.getCorrectCount(),
                entity.getWrongCount(),
                entity.getRankings()
        );

        // DERS SKORLARI BURADA:
        // Zaten 'JOIN FETCH' ile geldiği için tekrar lessonScoreRepo çağırmıyoruz!
        if (entity.getLessonScores() != null) {
            List<LessonScoreDto> scoreDtos = entity.getLessonScores().stream()
                    .map(ls -> new LessonScoreDto(ls.getLesson().getName(), ls.getCorrectCount(),
                            ls.getWrongCount(), ls.getNetCount())) // Örnek map
                    .collect(Collectors.toList());
            dto.setLessonScores(scoreDtos);
        }
        return dto;
    }
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



}
