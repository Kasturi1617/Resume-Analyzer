package com.example.resumeAnalyzer.demo.Controller;

import com.example.resumeAnalyzer.demo.Service.ResumeService;
import com.example.resumeAnalyzer.demo.dto.AnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<AnalysisResponseDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jd) throws IOException {
        return ResponseEntity.ok(resumeService.handleUploadAndAnalyze(file, jd));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<AnalysisResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getAnalysisByResumeId(id));
    }
}
