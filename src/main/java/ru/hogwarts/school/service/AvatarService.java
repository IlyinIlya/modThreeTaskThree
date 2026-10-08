package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar upload(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId).orElseThrow();

        Path directory = Path.of("avatars");
        Files.createDirectories(directory);
        Path filePath = directory.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());
        avatar.setStudent(student);

        return avatarRepository.save(avatar);
    }

    public Avatar get(Long id) {
        return avatarRepository.findById(id).orElseThrow();
    }
}