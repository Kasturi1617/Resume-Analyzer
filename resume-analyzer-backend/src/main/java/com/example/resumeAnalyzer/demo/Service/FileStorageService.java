package com.example.resumeAnalyzer.demo.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    @Value("${app.upload-dir}") private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        Path path = Paths.get(uploadDir);
        if(!Files.exists(path)) Files.createDirectories(path);

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = path.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }


}
