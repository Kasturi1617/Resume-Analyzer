package com.example.resumeAnalyzer.demo.Service;

import com.example.resumeAnalyzer.demo.dto.AnalysisResponseDto;
import com.example.resumeAnalyzer.demo.dto.ParseResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    @Autowired
    private ScoreService scoreService;

    private static final Map<String, List<String>> SKILL_TAXONOMY = Map.of(
            "backend", List.of("Spring Boot", "Java", "REST APIs", "Microservices", "Docker", "Kubernetes"),
            "frontend", List.of("React", "Angular", "JavaScript", "TypeScript", "HTML", "CSS"),
            "database", List.of("MySQL", "PostgreSQL", "MongoDB", "Redis", "Elasticsearch"),
            "devops", List.of("AWS", "Azure", "Jenkins", "Git", "CI/CD", "Terraform"),
            "ml", List.of("Python", "TensorFlow", "PyTorch", "Scikit-learn", "Pandas", "NumPy")
    );

    private static final Map<String, Integer> SKILL_MARKET_DEMAND = Map.of(
            "Spring Boot", 95, "Docker", 88, "Kubernetes", 85, "AWS", 92,
            "React", 90, "Python", 94, "Machine Learning", 87
    );

    public AnalysisResponseDto analyze(ParseResultDto parse, String jdText, Long resumeId) {
        AnalysisResponseDto analysisResponseDto = new AnalysisResponseDto();
        analysisResponseDto.resumeId = resumeId;
        analysisResponseDto.status = "DONE";
        analysisResponseDto.parserResult = parse;

        if (jdText != null && !jdText.isBlank()) {
            // Flow A: Resume + JD
            return analyzeWithJobDescription(analysisResponseDto, parse, jdText);
        } else {
            return analyzeResume(analysisResponseDto, parse);
        }
    }

    private AnalysisResponseDto analyzeWithJobDescription(AnalysisResponseDto resp,
                                                          ParseResultDto parse, String jdText) {
        // Step 1: Get skills from parser and JD
        Set<String> resumeSkills = normalizeSkills(parse.skills);
        Set<String> jdSkills = extractSkillsFromJD(jdText);

        // Step 2: CALL extractAdditionalSkillsFromText HERE to catch missed skills
        Set<String> additionalSkills = extractAdditionalSkillsFromText(parse.rawText, jdSkills);

        // Step 3: Merge all resume skills
        Set<String> allResumeSkills = new HashSet<>(resumeSkills);
        allResumeSkills.addAll(additionalSkills);

        // Debug logging
        System.out.println("=== DETAILED SKILL ANALYSIS ===");
        System.out.println("Raw resume skills from parser: " + parse.skills);
        System.out.println("Resume skills (normalized): " + resumeSkills);
        System.out.println("Additional skills from text: " + additionalSkills);
        System.out.println("All resume skills combined: " + allResumeSkills);
        System.out.println("JD skills (extracted): " + jdSkills);

        // Step 4: Find matches using the combined skill set
        Set<String> matched = new HashSet<>();
        for (String resumeSkill : allResumeSkills) {  // Use allResumeSkills instead of resumeSkills
            for (String jdSkill : jdSkills) {
                if (resumeSkill.equalsIgnoreCase(jdSkill)) {
                    matched.add(jdSkill);
                }
            }
        }

        Set<String> missing = new HashSet<>(jdSkills);
        missing.removeAll(matched);

        resp.skillsMatched = new ArrayList<>(matched);
        resp.skillsMissing = new ArrayList<>(missing);

        // Use advanced scoring engine
        resp.score = scoreService.calculateAdvancedScore(matched, jdSkills, parse);

        resp.recommendations = generateJobSpecificRecommendations(missing, parse, jdText);

        return resp;
    }



    private AnalysisResponseDto analyzeResume(AnalysisResponseDto analysisResponseDto, ParseResultDto parse) {
        analysisResponseDto.skillsMatched = parse.skills;
        analysisResponseDto.skillsMissing = Collections.emptyList();
        analysisResponseDto.score = scoreService.calculateResumeQualityScore(parse);

        analysisResponseDto.recommendations = generateResumeImprovementSuggestions(parse);

        return analysisResponseDto;
    }

    //    private Set<String> normalizeSkills(List<String> skills) {
//        if (skills == null) return new HashSet<>();
//
//        return skills.stream().map(this::normalizeSkillName).collect(Collectors.toSet());
//    }
// Update your normalizeSkills to detect more skills from resume text
    private Set<String> normalizeSkills(List<String> skills) {
        if (skills == null) return new HashSet<>();

        Set<String> normalizedSkills = skills.stream()
                .map(this::normalizeSkillName)
                .collect(Collectors.toSet());

        // Add this: Also check if skills list missed any obvious ones
        return normalizedSkills;
    }

    private Set<String> extractAdditionalSkillsFromText(String rawText, Set<String> jdSkills) {
        Set<String> additionalSkills = new HashSet<>();
        String lowerText = rawText.toLowerCase();

        for (String jdSkill : jdSkills) {
            String skillLower = jdSkill.toLowerCase();

            // Enhanced skill detection
            if (containsSkillVariation(lowerText, skillLower)) {
                additionalSkills.add(jdSkill);
            }
        }

        // Also check for skills that should definitely be there
        Map<String, String[]> commonSkills = Map.of(
                "Python", new String[]{"python", " py "},
                "JavaScript", new String[]{"javascript", "js", "node", "nodejs"},
                "REST APIs", new String[]{"rest", "api", "restful"},
                "Jenkins", new String[]{"jenkins", "ci/cd"},
                "Android", new String[]{"android", "kotlin"}
        );

        for (Map.Entry<String, String[]> entry : commonSkills.entrySet()) {
            if (jdSkills.contains(entry.getKey())) {
                for (String variation : entry.getValue()) {
                    if (lowerText.contains(variation)) {
                        additionalSkills.add(entry.getKey());
                        break;
                    }
                }
            }
        }

        return additionalSkills;
    }

    private boolean containsSkillVariation(String text, String skill) {
        return text.contains(skill) ||
                text.contains(skill.replace(" ", "")) ||
                text.contains(skill.replace(" ", "-"));
    }

    private String normalizeSkillName(String skill) {
        return skill.toLowerCase()
                .trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ");
    }

    // Update your extractSkillsFromJD method to be more comprehensive
    private Set<String> extractSkillsFromJD(String jdText) {
        Set<String> extractedSkills = new HashSet<>();
        String normalizedText = jdText.toLowerCase();

        // FIXED: Use Map.ofEntries() instead of Map.of() for more than 10 entries
        Map<String, List<String>> skillVariations = Map.ofEntries(
                Map.entry("Java", List.of("java", "jdk", "java programming")),
                Map.entry("Spring Boot", List.of("spring boot", "springboot", "spring framework", "spring")),
                Map.entry("Docker", List.of("docker", "containerization", "containers")),
                Map.entry("AWS", List.of("aws", "amazon web services", "cloud", "ec2", "s3")),
                Map.entry("React", List.of("react", "reactjs", "react.js")),
                Map.entry("Microservices", List.of("microservices", "microservice", "micro services")),
                Map.entry("Python", List.of("python", "py")),
                Map.entry("Node.js", List.of("node", "nodejs", "node.js")),
                Map.entry("JavaScript", List.of("javascript", "js", "ecmascript")),
                Map.entry("SQL", List.of("sql", "database", "mysql", "postgresql")),
                Map.entry("Git", List.of("git", "version control", "github", "gitlab")),
                Map.entry("Jenkins", List.of("jenkins", "ci/cd", "continuous integration"))
        );

        for (Map.Entry<String, List<String>> entry : skillVariations.entrySet()) {
            String skillName = entry.getKey();
            List<String> variations = entry.getValue();

            for (String variation : variations) {
                if (normalizedText.contains(variation)) {
                    extractedSkills.add(skillName);
                    break; // Found this skill, move to next
                }
            }
        }

        System.out.println("Skills found in JD: " + extractedSkills);
        return extractedSkills;
    }



    private boolean containsSkillWithContext(String text, String skill) {
        // Simple contains check first
        if (text.contains(skill)) {
            return true;
        }

        // Check common variations
        String[] variations = generateSkillVariations(skill);
        for (String variation : variations) {
            if (text.contains(variation)) {
                return true;
            }
        }

        return false;
    }

    private String[] generateSkillVariations(String skill) {
        Set<String> variations = new HashSet<>();
        variations.add(skill);
        variations.add(skill.replace(" ", ""));
        variations.add(skill.replace(" ", "-"));

        // Add specific variations for common skills
        if (skill.equals("spring boot")) {
            variations.addAll(List.of("springboot", "spring-boot", "spring framework"));
        } else if (skill.equals("javascript")) {
            variations.addAll(List.of("js", "node.js", "nodejs"));
        }

        return variations.toArray(new String[0]);
    }


//    private String[] generatedSkillVariations(String skill) {
//        // Generate common variations (e.g., "Spring Boot" -> ["spring boot", "springboot"])
//        return new String[]{
//                skill,
//                skill.replace(" ", ""),
//                skill.replace(" ", "-"),
//                skill + " framework",
//                skill + " technology"
//        };
//    }

    private List<String> generateJobSpecificRecommendations(Set<String> missingSkills, ParseResultDto parse, String jdText) {
        List<String> recommendations = new ArrayList<>();
        // Prioritize missing skills by importance
        List<String> prioritizedSkills = missingSkills.stream()
                .sorted((a, b) -> getSkillImportance(b) - getSkillImportance(a))
                .limit(5)
                .collect(Collectors.toList());

        for (String skill : prioritizedSkills) {
            recommendations.add("Learn " + skill + " to match job requirements - high priority for this role");
        }
        recommendations.addAll(generateRoleSpecificSuggestions(jdText, parse));
        return recommendations;
    }

    private int getSkillImportance(String skill) {
        // Simple importance scoring - can be enhanced with market data
        Map<String, Integer> importance = Map.of(
                "Java", 95, "Spring Boot", 95, "Python", 90, "Docker", 85,
                "AWS", 90, "React", 88, "SQL", 85, "Git", 80
        );
        return importance.getOrDefault(skill, 50);
    }

    private List<String> generateRoleSpecificSuggestions(String jdText, ParseResultDto parse) {
        List<String> suggestions = new ArrayList<>();
        String lowerJD = jdText.toLowerCase();

        if (lowerJD.contains("senior") || lowerJD.contains("lead")) {
            suggestions.add("👨‍💼 Highlight leadership and mentoring experience for senior roles");
            suggestions.add("📋 Emphasize project management and team coordination skills");
        }
        if (lowerJD.contains("startup") || lowerJD.contains("fast-paced")) {
            suggestions.add("⚡ Showcase adaptability and ability to wear multiple hats");
        }
        return suggestions;
    }

    private List<String> generateResumeImprovementSuggestions(ParseResultDto parse) {
        List<String> suggestions = new ArrayList<>();

        suggestions.addAll(analyzeStructuralIssues(parse));
        suggestions.addAll(analyzeContentIssues(parse));
        suggestions.addAll(analyzeSkillsIssues(parse));
        suggestions.addAll(generateCareerAdvice(parse));

        return suggestions.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<String> analyzeStructuralIssues(ParseResultDto parse) {
        List<String> suggestions = new ArrayList<>();
        if (parse.emails == null || parse.emails.isEmpty()) {
            suggestions.add("📧 Add a professional email address to your contact information");
        }

        if (parse.phones == null || parse.phones.isEmpty()) {
            suggestions.add("📱 Include a phone number for easy contact");
        }

        if (parse.experience == null || parse.experience.isEmpty()) {
            suggestions.add("💼 Add detailed work experience with specific achievements");
        }

        int wordCount = parse.rawText.split("\\s+").length;
        if (wordCount > 800) {
            suggestions.add("📄 Resume is too long (" + wordCount + " words). Aim for 400-600 words");
        } else if (wordCount < 200) {
            suggestions.add("📄 Resume seems brief. Add more details about your achievements");
        }

        return suggestions;
    }

    private List<String> analyzeContentIssues(ParseResultDto parse) {
        List<String> suggestions = new ArrayList<>();
        String rawText = parse.rawText;

        if (!hasQuantifiableAchievements(rawText)) {
            suggestions.add("📊 Add quantifiable achievements (e.g., 'Improved performance by 30%', 'Led team of 5')");
        }

        if (!hasActionVerbs(rawText)) {
            suggestions.add("⚡ Use strong action verbs like 'developed', 'implemented', 'optimized', 'led'");
        }

        if (containsPersonalPronouns(rawText)) {
            suggestions.add("✏️ Remove personal pronouns (I, me, my) for more professional tone");
        }

        return suggestions;
    }

    private List<String> analyzeSkillsIssues(ParseResultDto parse) {
        List<String> suggestions = new ArrayList<>();

        if (parse.skills == null || parse.skills.size() < 5) {
            suggestions.add("🔧 Add more relevant technical skills to strengthen your profile");
        }

        // Suggest trending skills based on detected role
        String detectedRole = detectRoleFromText(parse.rawText);
        List<String> trendingSkills = getTrendingSkillsForRole(detectedRole);

        for (String trendingSkill : trendingSkills.subList(0, Math.min(2, trendingSkills.size()))) {
            if (!containsSkillIgnoreCase(parse.skills, trendingSkill)) {
                suggestions.add("🚀 Consider learning " + trendingSkill + " - high market demand in " + detectedRole + " roles");
            }
        }

        return suggestions;
    }

    private List<String> generateCareerAdvice(ParseResultDto parse) {
        List<String> advice = new ArrayList<>();

        advice.add("🎯 Tailor your resume for each job application");
        advice.add("🔗 Add links to your portfolio, GitHub, or LinkedIn profile");
        advice.add("📈 Focus on impact and results rather than just responsibilities");

        return advice;
    }

    // Utility helper methods
    private boolean hasQuantifiableAchievements(String text) {
        String[] patterns = {"\\d+%", "\\d+\\s*(years?|months?)", "\\$\\d+", "\\d+\\s*(users?|customers?|projects?)"};
        return Arrays.stream(patterns)
                .anyMatch(pattern -> text.matches(".*" + pattern + ".*"));
    }

    private boolean hasActionVerbs(String text) {
        String[] actionVerbs = {"developed", "implemented", "created", "led", "managed", "built",
                "designed", "optimized", "improved", "achieved", "delivered"};
        String lowerText = text.toLowerCase();
        return Arrays.stream(actionVerbs)
                .anyMatch(lowerText::contains);
    }

    private boolean containsPersonalPronouns(String text) {
        String[] pronouns = {" i ", " me ", " my ", " mine "};
        String lowerText = " " + text.toLowerCase() + " ";
        return Arrays.stream(pronouns)
                .anyMatch(lowerText::contains);
    }

    private String detectRoleFromText(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.contains("backend") || lowerText.contains("spring") || lowerText.contains("java")) {
            return "backend";
        } else if (lowerText.contains("frontend") || lowerText.contains("react") || lowerText.contains("javascript")) {
            return "frontend";
        } else if (lowerText.contains("machine learning") || lowerText.contains("python") || lowerText.contains("data")) {
            return "ml";
        } else if (lowerText.contains("devops") || lowerText.contains("docker") || lowerText.contains("aws")) {
            return "devops";
        }
        return "general";
    }

    private List<String> getTrendingSkillsForRole(String role) {
        Map<String, List<String>> trendingByRole = Map.of(
                "backend", List.of("Spring Boot", "Docker", "Kubernetes", "Microservices", "AWS"),
                "frontend", List.of("React", "TypeScript", "Next.js", "Tailwind CSS", "GraphQL"),
                "ml", List.of("PyTorch", "TensorFlow", "MLOps", "Docker", "Kubernetes"),
                "devops", List.of("Kubernetes", "Terraform", "AWS", "Docker", "Jenkins"),
                "general", List.of("Docker", "Git", "Linux", "SQL", "Python")
        );
        return trendingByRole.getOrDefault(role, trendingByRole.get("general"));
    }

    private boolean containsSkillIgnoreCase(List<String> skills, String targetSkill) {
        if (skills == null) return false;
        return skills.stream()
                .anyMatch(skill -> skill.equalsIgnoreCase(targetSkill));
    }
}

