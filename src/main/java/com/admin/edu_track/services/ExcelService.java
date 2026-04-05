package com.admin.edu_track.services;

import com.admin.edu_track.embeddings.Rankings;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.requestDto.ExamResultRequestDto;
import com.admin.edu_track.responseDto.LessonScoreDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // İlk sayfayı oku

        // İlk satır başlık (header) olduğu için 1. satırdan başlıyoruz
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) continue;

            try {
                ExamResultRequestDto dto = new ExamResultRequestDto();
                dto.setExamId(examId);

                // No (Sütun 1) - String veya Sayı olabilir, garantiye alalım
                dto.setStudentId(null); // Servis katmanında studentNumber ile bulacağız
                String studentNo = getCellValueAsString(row.getCell(1));

                // Puanlar ve Netler
                dto.setLgsScore(getCellValueAsDouble(row.getCell(25)));
                dto.setNetCount(getCellValueAsDouble(row.getCell(24)));
                dto.setTotalCorrect((int) getCellValueAsDouble(row.getCell(22)));
                dto.setTotalWrong((int) getCellValueAsDouble(row.getCell(23)));

                // Rankings (S, O, İlçe, İl)
                Rankings rankings = new Rankings();
                rankings.setClassRank((int) getCellValueAsDouble(row.getCell(26)));
                rankings.setSchoolRank((int) getCellValueAsDouble(row.getCell(27)));
                rankings.setDistrictRank((int) getCellValueAsDouble(row.getCell(28)));
                rankings.setCityRank((int) getCellValueAsDouble(row.getCell(29)));
                dto.setRankings(rankings);

                // Dersler (Burada senin LessonScoreDto listeni dolduracağız)
                dto.setLessonScores(extractLessonScores(row));

                // DAHA ÖNCE YAZDIĞIN SERVİSİ KULLANIYORUZ!
                // Not: saveExamResult metodunu studentNumber ile arama yapacak şekilde küçük bir güncelleyeceğiz
                examResultService.saveExamResultWithNo(dto, studentNo);
                count++;
            } catch (Exception e) {
                System.err.println("Satır " + i + " işlenirken hata oluştu: " + e.getMessage());
            }
        }
        workbook.close();
        return count;
    }

    public List<Student> parseStudentExcel(InputStream input) throws IOException {
        List<Student> students = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheetAt(0);       // İlk sayfayı oku

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Student student = new Student();
                student.setName(getCellValueAsString(row.getCell(0)));
                student.setSurname(getCellValueAsString(row.getCell(1)));

                // Sayısal hücrelerde "CellType" kontrolü yapmak güvenlidir
                if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                    student.setStudentNumber(String.valueOf((int) row.getCell(2).getNumericCellValue()));
                } else {
                    student.setStudentNumber(row.getCell(2).getStringCellValue());
                }
                // Cinsiyet vb. diğer alanlar eklenebilir
                students.add(student);
            }
        }
        catch (IOException e){
            throw new RuntimeException("Excel dosyası okunurken hata oluştu: " + e.getMessage());
        }
        return students;

    }

    // Hücre tipine göre güvenli veri okuma yardımcı metodları
    private String getCellValueAsString(Cell cell) {
/*
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }

        // Eğer hücre sayısal ise (Örn: 501.0)
        if (cell.getCellType() == CellType.NUMERIC) {
            // Sayı değerini al ve Double'dan kurtulmak için formatla
            // DecimalFormat kullanarak .0 kısmını atıyoruz
            double numericValue = cell.getNumericCellValue();
            return String.format("%.0f", numericValue);
        }

        // Eğer zaten metin ise direkt oku
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
*/
        // Diğer durumlarda (Formül vb.) DataFormatter kullanmak en modern yaklaşımdır
        return dataFormatter.formatCellValue(cell).trim();
    }

    private double getCellValueAsDouble(Cell cell) {
        /*if (cell == null || cell.getCellType() == CellType.BLANK) return 0.0;
        return cell.getNumericCellValue();
            */
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0.0;
        }

        // Eğer hücre zaten sayı formatındaysa direkt oku
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        // Eğer hücre metin (String) formatındaysa, içindeki yazıyı sayıya çevirmeyi dene
        if (cell.getCellType() == CellType.STRING) {
            try {
                String value = cell.getStringCellValue().trim().replace(",", "."); // Virgül varsa noktaya çevir
                return value.isEmpty() ? 0.0 : Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.err.println("Sayıya çevirme hatası: " + cell.getStringCellValue());
                return 0.0;
            }
        }

        // Formül içeren bir hücreyse, hesaplanmış sonucu almayı dene
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
        return firstCell == null || firstCell.getCellType() == CellType.BLANK;
    }
    // ExcelService.java içine eklenecek yardımcı metot
    private List<LessonScoreDto> extractLessonScores(Row row) {
        List<LessonScoreDto> scores = new ArrayList<>();

        // 1. TÜRKÇE (TD: 4, TY: 5, TN: 6)
        scores.add(createLessonDto("Turkce", row, 4, 5, 6));

        // 2. SOSYAL (SD: 7, SY: 8, SN: 9)
        scores.add(createLessonDto("Sosyal", row, 7, 8, 9));

        // 3. DİN KÜLTÜRÜ (DD: 10, DY: 11, DN: 12)
        scores.add(createLessonDto("Din Kulturu", row, 10, 11, 12));

        // 4. İNGİLİZCE (İD: 13, İY: 14, İN: 15)
        scores.add(createLessonDto("Ingilizce", row, 13, 14, 15));

        // 5. MATEMATİK (MD: 16, MY: 17, MN: 18)
        scores.add(createLessonDto("Matematik", row, 16, 17, 18));

        // 6. FEN BİLİMLERİ (FD: 19, FY: 20, FN: 21)
        scores.add(createLessonDto("Fen Bilgisi", row, 19, 20, 21));

        return scores;
    }

    // Tekrardan kurtulmak için küçük bir "Creator" metod
    private LessonScoreDto createLessonDto(String name, Row row, int dCol, int yCol, int nCol) {
        LessonScoreDto dto = new LessonScoreDto();
        dto.setLessonName(name);
        dto.setCorrectCount((int) getCellValueAsDouble(row.getCell(dCol)));
        dto.setWrongCount((int) getCellValueAsDouble(row.getCell(yCol)));
        dto.setNetCount(getCellValueAsDouble(row.getCell(nCol)));
        return dto;
    }
}