package com.admin.edu_track.controllers;


import com.admin.edu_track.entities.ExamResult;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.ExamResultResponseDto;
import com.admin.edu_track.services.ExamResultService;
import com.admin.edu_track.services.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("api/results")
/*@CrossOrigin(
        origins = {
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        }
)*/
@RequiredArgsConstructor
public class ExamResultController {

    private final ExamResultService resultService;
    private final ExcelService excelService;

    @GetMapping("/search")
    public ResponseEntity<List<ExamResultResponseDto>> getAllExamResults(@RequestParam(required = false) Long studentId,
                                                                         @RequestParam(required = false) Long examId){
        return ResponseEntity.ok(resultService.getAllExamResultsDto(studentId, examId));
    }
    @PostMapping()
    public ResponseEntity<ExamResult> createExamResult(@RequestBody ExamResultRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(resultService.saveExamResult(dto));
    }

    @PostMapping("/upload-excel/{examId}")
    public ResponseEntity<String> uploadExcel(
            @PathVariable Long examId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Lütfen geçerli bir Excel dosyası seçiniz.");
        }

        try {
            // Excel'i işle ve kaç kayıt eklendiğini dön
            int savedCount = excelService.importExamResults(file, examId);

            return ResponseEntity.ok(savedCount + " öğrencinin sınav sonucu başarıyla sisteme işlendi.");

        } catch (Exception e) {
            // Hata durumunda (dosya formatı yanlışsa vb.)
            return ResponseEntity.internalServerError()
                    .body("Dosya işlenirken bir hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResultResponseDto> updateResult(@PathVariable Long id, @RequestBody ExamResultRequestDto result){
        return ResponseEntity.ok(resultService.updateExamResult(id, result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamResult(@PathVariable Long id){
        if (resultService.deleteExamResult(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
