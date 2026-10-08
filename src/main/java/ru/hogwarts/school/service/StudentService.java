package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(Student student) {
        return studentRepository.save(student);
    }

    public Student get(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student update(Long id, Student student) {
        student.setId(id);
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getByAge(Integer min, Integer max) {
        if (min != null && max != null) {
            return studentRepository.findByAgeBetween(min, max);
        }
        if (min != null) {
            return studentRepository.findByAgeGreaterThanEqual(min);
        }
        if (max != null) {
            return studentRepository.findByAgeLessThanEqual(max);
        }
        return studentRepository.findAll();
    }

    public Faculty getFaculty(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        return student.getFaculty();
    }
}
