package com.admin.edu_track.services;

import com.admin.edu_track.embeddings.Rankings;
import com.admin.edu_track.embeddings.ScoreMetrics;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.LessonScoreDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ExamResultService examResultService;
    private final DataFormatter dataFormatter = new DataFormatter();

    public int importExamResults(MultipartFile file, Long examId) throws IOException {
        int count = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                try {
                    ExamResultRequestDto dto = new ExamResultRequestDto();
                    dto.setExamId(examId);

                    // No (Sütun 1)
                    dto.setStudentId(null);
                    String studentNo = getCellValueAsString(row.getCell(1));
                    if (studentNo.isEmpty()) continue;

                    // LGS Puanı (Sütun 25)
                    dto.setLgsScore(getCellValueAsDouble(row.getCell(25)));

                    // Toplam Skor Metrikleri (Doğru: 22, Yanlış: 23, Net: 24)
                    int totalCorrect = (int) getCellValueAsDouble(row.getCell(22));
                    int totalWrong = (int) getCellValueAsDouble(row.getCell(23));
                    double netCount = getCellValueAsDouble(row.getCell(24));

                    ScoreMetrics totalMetrics = new ScoreMetrics();
                    totalMetrics.setCorrectCount(totalCorrect);
                    totalMetrics.setWrongCount(totalWrong);
                    totalMetrics.setNetCount(netCount);
                    dto.setScoreMetrics(totalMetrics);

                    // Sıralamalar (S: 26, O: 27, İlçe: 28, İl: 29)
                    Rankings rankings = new Rankings();
                    rankings.setClassRank((int) getCellValueAsDouble(row.getCell(26)));
                    rankings.setSchoolRank((int) getCellValueAsDouble(row.getCell(27)));
                    rankings.setDistrictRank((int) getCellValueAsDouble(row.getCell(28)));
                    rankings.setCityRank((int) getCellValueAsDouble(row.getCell(29)));
                    dto.setRankings(rankings);

                    // Ders Skorları Listesi
                    dto.setLessonScores(extractLessonScores(row));

                    examResultService.saveExamResultWithNo(dto, studentNo);
                    count++;
                } catch (Exception e) {
                    System.err.println("Satır " + i + " işlenirken hata oluştu: " + e.getMessage());
                }
            }
        }
        return count;
    }

    public List<Student> parseStudentExcel(InputStream input) throws IOException {
        List<Student> students = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                Student student = new Student();
                student.setName(getCellValueAsString(row.getCell(0)));
                student.setSurname(getCellValueAsString(row.getCell(1)));
                student.setStudentNumber(getCellValueAsString(row.getCell(2)));

                students.add(student);
            }
        }
        return students;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        return dataFormatter.formatCellValue(cell).trim();
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0.0;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        if (cell.getCellType() == CellType.STRING) {
            try {
                String value = cell.getStringCellValue().trim().replace(",", ".");
                return value.isEmpty() ? 0.0 : Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getNumericCellValue();
            } catch (Exception e) {
                return 0.0;
            }
        }

        return 0.0;
    }

    private boolean isRowEmpty(Row row) {
        Cell firstCell = row.getCell(1);
        if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
            return true;
        }
        return getCellValueAsString(firstCell).isEmpty();
    }

    private List<LessonScoreDto> extractLessonScores(Row row) {
        List<LessonScoreDto> scores = new ArrayList<>();

        scores.add(createLessonDto("Turkce", row, 4, 5, 6));
        scores.add(createLessonDto("Sosyal", row, 7, 8, 9));
        scores.add(createLessonDto("Din Kulturu", row, 10, 11, 12));
        scores.add(createLessonDto("Ingilizce", row, 13, 14, 15));
        scores.add(createLessonDto("Matematik", row, 16, 17, 18));
        scores.add(createLessonDto("Fen Bilgisi", row, 19, 20, 21));

        return scores;
    }

    private LessonScoreDto createLessonDto(String name, Row row, int dCol, int yCol, int nCol) {
        LessonScoreDto dto = new LessonScoreDto();
        dto.setLessonName(name);

        int correct = (int) getCellValueAsDouble(row.getCell(dCol));
        int wrong = (int) getCellValueAsDouble(row.getCell(yCol));
        double net = getCellValueAsDouble(row.getCell(nCol));

        // Hem ScoreMetrics nesnesini hem de varsa düz alanları doldurur
        ScoreMetrics metrics = new ScoreMetrics();
        metrics.setCorrectCount(correct);
        metrics.setWrongCount(wrong);
        metrics.setNetCount(net);
        dto.setScoreMetrics(metrics);

        // LessonScoreDto içinde düz alanlar da tanımlıysa garantiye alalım:
        // dto.setCorrectCount(correct);
        // dto.setWrongCount(wrong);
        // dto.setNetCount(net);

        return dto;
    }
}