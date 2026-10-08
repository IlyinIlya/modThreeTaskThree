package ru.hogwarts.school.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long upload(@PathVariable Long studentId,
                       @RequestParam ("file") MultipartFile file) throws IOException {
        return avatarService.upload(studentId, file).getId();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFromDatabase(@PathVariable Long id) {
        Avatar avatar = avatarService.get(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getData());
    }

    @GetMapping("/{id}/from-file")
    public ResponseEntity<byte[]> getFromFile(@PathVariable Long id) throws IOException {
        Avatar avatar = avatarService.get(id);
        byte[] data = Files.readAllBytes(Path.of(avatar.getFilePath()));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(data);
    }
}