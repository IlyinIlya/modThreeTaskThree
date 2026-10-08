package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty create(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty get(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty update(Long id, Faculty faculty) {
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public void delete(Long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getByNameOrColor(String name, String color) {
        if (name != null && color != null) {
            return facultyRepository.findByNameIgnoreCaseAndColorIgnoreCase(name, color);
        }
        if (name != null) {
            return facultyRepository.findByNameIgnoreCase(name);
        }
        if (color != null) {
            return facultyRepository.findByColorIgnoreCase(color);
        }
        return facultyRepository.findAll();
    }

    public List<Faculty> getByNameOrColor(String name) {
        if (name != null) {
            return facultyRepository
                    .findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(name, name);
        }
        return facultyRepository.findAll();
    }

    public List<Student> getStudents(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        if (faculty == null) {
            return null;
        }
        return faculty.getStudents();
    }
}