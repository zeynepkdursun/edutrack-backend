package com.admin.edu_track;

import com.admin.edu_track.entities.*;
import com.admin.edu_track.repositories.*;
import com.admin.edu_track.requestDto.ExamRequestDto;
import com.admin.edu_track.services.ExamService;
import com.admin.edu_track.services.SchoolClassService;
import com.admin.edu_track.services.StudentRegistryService;
import com.admin.edu_track.services.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class EduTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduTrackApplication.class, args);
	}

	@Bean
	public CommandLineRunner addTestData(StudentService studentService,
										 SchoolClassService classService,
										 AcademicYearRepository yearRepo,
										 StudentRegistryService registryService,
										 ExamService examService,
										 ExamResultRepository examResultRepo
	) {
		return args -> {
			if(classService.getAllClasses().isEmpty()){
				System.out.println("DEMEMEEEEEEEEEEEEEEEEEEEEEeee");
				AcademicYear year = createAcademicYear(yearRepo, "2024-2025", true);
				Long yearId = 2L;

				SchoolClass class8A = createClass(classService, 8, "A");
				SchoolClass class7B = createClass(classService, 7, "B");

				//8-A sinifi icin
				Student ali = createStudent(studentService, "Ali", "Yılmaz", "500");
				Student ayse = createStudent(studentService, "Ayşe", "Demir", "501");

				//7-B sinifi icin
				Student mehmet = createStudent(studentService, "Mehmet", "Kaya", "400");

				//Enrollments
				registerStudent(registryService, ali, class8A, year);
				registerStudent(registryService, ayse, class8A, year);
				registerStudent(registryService, mehmet, class7B, year);

				/// Exam
				Exam deneme1 = createExam(examService, "Ozdebir Ekim", yearId, 8, LocalDate.of(2026, 2, 15));

				//createResult(examResultRepo, ali, deneme1);
				//createResult(examResultRepo, ayse, deneme1);
				//createResult(examResultRepo, mehmet, deneme1);

				System.out.println(">>> Test verileri (2 Sınıf, 3 Öğrenci, 1 Sınav) yüklendi!");}
			else{
				System.out.println("db is fulllllll");
			}


			//System.out.println("test data is successfully loaded");

			//System.out.println("db is already full with ali");

		};
	}

	private Student createStudent(StudentService studentService, String name, String surname, String no) {
		Student s = new Student();
		s.setName(name);
		s.setSurname(surname);
		s.setStudentNumber(no);
		return studentService.saveStudent(s);
	}
	private void registerStudent(StudentRegistryService registryService, Student s, SchoolClass c, AcademicYear y) {
		StudentRegistry e = new StudentRegistry();
		e.setStudent(s);
		e.setSchoolClass(c);
		e.setAcademicYear(y);
		registryService.createRegistry(e);
	}
	private Exam createExam(ExamService examService, String title, Long yearId, int level, LocalDate d){
		ExamRequestDto e = new ExamRequestDto();
		e.setTitle(title);
		e.setAcademicYearId(yearId);
		e.setLevel(level);
		//e.setDate(new Date(2025, 1, 15));
		e.setDate(d);
		return examService.saveExam(e);
	}
	private void createResult(ExamResultRepository repo, Student s, Exam e) {
		ExamResult r = new ExamResult();
		r.setStudent(s);
		r.setExam(e);
		repo.save(r);
	}
	private AcademicYear createAcademicYear(AcademicYearRepository repo, String label, boolean isActive) {
		AcademicYear y = new AcademicYear();
		y.setLabel(label);
		y.setActive(isActive);
		return repo.save(y);
	}
	private SchoolClass createClass(SchoolClassService schoolClassService, int level, String branch){
		SchoolClass c = new SchoolClass();
		c.setLevel(level);
		c.setBranch(branch);
		return schoolClassService.createClass(c);
	}

}
