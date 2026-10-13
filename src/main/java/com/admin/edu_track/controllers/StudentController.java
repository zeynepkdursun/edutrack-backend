package com.admin.edu_track.controllers;
import com.admin.edu_track.entities.Student;
import com.admin.edu_track.responseDto.StudentResponseDto;
import com.admin.edu_track.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> getAllStudents(@RequestParam(value = "activeOnly", required = false, defaultValue = "true") Boolean activeOnly){
        return ResponseEntity.ok(studentService.getAllStudentsDto(activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id){
        return ResponseEntity.ok(studentService.getStudentById(id));
    }


    // api/students/search?student-no=500&yearId=2&level=7
    @GetMapping("/search")
    public ResponseEntity<List<Student>> getStudentByStudentNumber(@RequestParam(required = false, name ="student-no") String studentNo,
                                                                   @RequestParam(required = false) Long yearId,
                                                                   @RequestParam(required = false) Integer level) {
        List<Student> students = studentService.searchStudents(yearId, level, studentNo);
        return ResponseEntity.ok(students);
    }

    //FETCH STUDENTS FROM A SPECIFIC CLASS -> get students from the class 8-A in the current academic year
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Student>> getCurrentStudentsByClass(@PathVariable Long classId){
        return ResponseEntity.ok(studentService.getCurrentStudentsByClassId(classId));
    }



    /*@PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody @Valid Student student){
        Student createdStudent = studentService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        //return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }*/

    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long studentId, @Valid @RequestBody Student student){
        Student updatedStudent = studentService.updateStudent(studentId, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId){
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

/*
    @PostMapping("/upload") // Dosya ile toplu yükleme
    public List<Student> uploadStudents(...)*/
}
