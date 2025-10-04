package com.example.resumeAnalyzer.demo.Service;

import com.example.resumeAnalyzer.demo.dto.AnalysisResponseDto;
import com.example.resumeAnalyzer.demo.dto.ParseResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final FileStorageService fileStorageService;
    private final ParserClient parserClient;
    private final AnalysisService analysisService;

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, AnalysisResponseDto> memoryDb = new ConcurrentHashMap<>();

    public AnalysisResponseDto handleUploadAndAnalyze(MultipartFile file, String jd) throws IOException {
        Long resumeId = idCounter.getAndIncrement();
        fileStorageService.saveFile(file);
        ParseResultDto parsed = parserClient.parse(file);
        AnalysisResponseDto result = analysisService.analyze(parsed, jd, resumeId);
        memoryDb.put(resumeId, result);
        return result;
    }

    public AnalysisResponseDto getAnalysisByResumeId(Long id) {
        return memoryDb.get(id);
    }
}

