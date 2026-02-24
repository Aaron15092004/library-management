package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.StudentDAO;
import com.group6.librarymanager.model.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentApiController {

    private final StudentDAO studentDAO;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return studentDAO.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Student student = new Student();
        student.setStudentName((String) body.get("studentName"));
        student.setEmail((String) body.get("email"));
        student.setPhone((String) body.get("phone"));
        Student saved = studentDAO.save(student);
        return ResponseEntity.ok(toMap(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        return studentDAO.findById(id).map(student -> {
            if (body.containsKey("studentName"))
                student.setStudentName((String) body.get("studentName"));
            if (body.containsKey("email"))
                student.setEmail((String) body.get("email"));
            if (body.containsKey("phone"))
                student.setPhone((String) body.get("phone"));
            Student saved = studentDAO.save(student);
            return ResponseEntity.ok(toMap(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!studentDAO.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentDAO.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(Student s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("studentId", s.getStudentId());
        map.put("studentName", s.getStudentName());
        map.put("email", s.getEmail());
        map.put("phone", s.getPhone());
        return map;
    }
}
