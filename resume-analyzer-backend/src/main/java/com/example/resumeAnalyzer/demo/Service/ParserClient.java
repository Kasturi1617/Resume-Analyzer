package com.example.resumeAnalyzer.demo.Service;

import com.example.resumeAnalyzer.demo.dto.ParseResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiPanelUI;
import java.io.IOException;
import org.springframework.http.HttpHeaders;

@Service
public class ParserClient {
    @Value("${app.parser-url}") private String parseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ParseResultDto parse(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()){
            @Override public String getFilename() {
                return file.getOriginalFilename();
            }
            };
        body.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(parseUrl, request, ParseResultDto.class);
    }
}
