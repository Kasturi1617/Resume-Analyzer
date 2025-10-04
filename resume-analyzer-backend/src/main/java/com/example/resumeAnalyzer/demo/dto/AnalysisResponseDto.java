package com.example.resumeAnalyzer.demo.dto;

import java.util.List;

public class AnalysisResponseDto {
    public Long resumeId;
    public String status;
    public Integer score;
    public List<String> skillsMatched;
    public List<String> skillsMissing;
    public List<String> recommendations;
    public ParseResultDto parserResult;
}
