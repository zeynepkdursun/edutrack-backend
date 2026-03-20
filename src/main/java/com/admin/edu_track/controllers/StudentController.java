package com.admin.edu_track.controllers;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.responses.StudentResponseDto;
import com.admin.edu_track.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/students")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    /*@GetMapping
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) Long yearId,
                                                        @RequestParam(required = false) Long classId){
        return ResponseEntity.ok(studentService.getAllStudents(yearId, classId));
        //return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
    @GetMapping("/all-details")
    public List<StudentResponseDto> getActiveStudents() {
        return studentService.getAllStudentsDto();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> getStudentByStudentNumber(@RequestParam(required = false, name ="student-no") String studentNo,
                                                                   @RequestParam(required = false) Long yearId,
                                                                   @RequestParam(required = false) Integer level) {
        List<Student> students = studentService.searchStudents(yearId, level, studentNo);
        return ResponseEntity.ok(students);
    }

    //FETCH STUDENTS FROM A SPECIFIC CLASS -> 8-A sınıfındaki öğrencileri getir.
    // Guncel egitim ogretim yilindaki
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Student>> getStudentsByClass(@PathVariable Long classId){
        List<Student> students = studentService.getStudentsByClassId(classId);
        if (students.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(students);
    }


    //ayni yilda ayni studentNoya sahip ogrenci create edilemesin (LATER))))))))))))))))))
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        Student createdStudent = studentService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        //return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);


    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long studentId, @RequestBody Student student){
        Student updatedStudent = studentService.updateStudent(studentId, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId){
        if(studentService.deleteStudent(studentId)){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }

    }

/*
    @PostMapping("/upload") // Dosya ile toplu yükleme
    public List<Student> uploadStudents(...)*/
}
