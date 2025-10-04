package com.example.resumeAnalyzer.demo.Service;

import com.example.resumeAnalyzer.demo.dto.ParseResultDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ScoreService {

    // Market demand scores for different skills
    private static final Map<String, Integer> SKILL_MARKET_DEMAND = Map.ofEntries(
            Map.entry("Spring Boot", 95),
            Map.entry("Java", 90),
            Map.entry("Docker", 88),
            Map.entry("Kubernetes", 85),
            Map.entry("AWS", 92),
            Map.entry("React", 90),
            Map.entry("Python", 94),
            Map.entry("Machine Learning", 87),
            Map.entry("SQL", 85),
            Map.entry("JavaScript", 88),
            Map.entry("Git", 82),
            Map.entry("REST APIs", 86),
            Map.entry("Microservices", 89),
            Map.entry("TypeScript", 83),
            Map.entry("Node.js", 84),
            Map.entry("MongoDB", 80),
            Map.entry("PostgreSQL", 82)
    );

    public Integer calculateAdvancedScore(Set<String> matchedSkills, Set<String> jdSkills,
                                                 ParseResultDto parse) {
        double skillScore = calculateSkillMatchScore(matchedSkills, jdSkills);
        double experienceScore = calculateExperienceScore(parse);
        double contentQualityScore = calculateContentQualityScore(parse);

        // Weighted scoring: Skills(50%) + Experience(30%) + Content Quality(20%)
        double finalScore = (skillScore * 0.5) + (experienceScore * 0.3) + (contentQualityScore * 0.2);

        return Math.min(100, (int) Math.round(finalScore));
    }

    public Integer calculateResumeQualityScore(ParseResultDto parse) {
        double structureScore = calculateStructureScore(parse);
        double skillDiversityScore = calculateSkillDiversityScore(parse);
        double contentRichnessScore = calculateContentRichnessScore(parse);

        double finalScore = (structureScore * 0.4) + (skillDiversityScore * 0.3) +
                (contentRichnessScore * 0.3);

        return (int) Math.round(finalScore);
    }

    private double calculateSkillMatchScore(Set<String> matched, Set<String> required) {
        if (required.isEmpty()) return 80.0;

        double basicMatch = (double) matched.size() / required.size() * 100;

        // Bonus for high-demand skills
        double demandBonus = matched.stream()
                .mapToInt(skill -> SKILL_MARKET_DEMAND.getOrDefault(skill, 0))
                .average()
                .orElse(0) * 0.1;

        return Math.min(100.0, basicMatch + demandBonus);
    }

    private double calculateExperienceScore(ParseResultDto parse) {
        if (parse.experience == null || parse.experience.isEmpty()) return 20.0;

        // Analyze experience depth and relevance
        int experienceYears = extractExperienceYears(parse.rawText);
        double experienceScore = Math.min(100.0, experienceYears * 10);

        // Bonus for progression and achievements
        if (hasCareerProgression(parse.rawText)) {
            experienceScore += 15;
        }

        return Math.min(100.0, experienceScore);
    }

    private double calculateStructureScore(ParseResultDto parse) {
        double score = 0.0;
        double maxScore = 40.0;

        // Contact information (10 points)
        if (parse.emails != null && !parse.emails.isEmpty()) score += 5.0;
        if (parse.phones != null && !parse.phones.isEmpty()) score += 5.0;

        // Essential sections (30 points)
        if (parse.experience != null && !parse.experience.isEmpty()) score += 15.0;
        if (parse.education != null && !parse.education.isEmpty()) score += 10.0;
        if (parse.skills != null && parse.skills.size() >= 3) score += 5.0;

        return Math.min(maxScore, score);
    }

    private double calculateSkillDiversityScore(ParseResultDto parse) {
        if (parse.skills == null || parse.skills.isEmpty()) return 0.0;

        double maxScore = 30.0;
        Set<String> skillCategories = new HashSet<>();

        // Categorize skills into different domains
        for (String skill : parse.skills) {
            skillCategories.add(categorizeSkill(skill.toLowerCase()));
        }

        // Score based on diversity (more categories = better)
        double diversityScore = Math.min(maxScore, skillCategories.size() * 6.0);

        // Bonus for having trending/high-demand skills
        long highDemandSkills = parse.skills.stream()
                .mapToLong(skill -> SKILL_MARKET_DEMAND.getOrDefault(skill, 0) > 85 ? 1 : 0)
                .sum();

        diversityScore += Math.min(10.0, highDemandSkills * 2.0);

        return Math.min(maxScore, diversityScore);
    }

    private double calculateContentRichnessScore(ParseResultDto parse) {
        double score = 0.0;
        double maxScore = 30.0;
        String rawText = parse.rawText;

        // Word count analysis (10 points)
        int wordCount = rawText.split("\\s+").length;
        if (wordCount >= 200 && wordCount <= 600) {
            score += 10.0;
        } else if (wordCount > 100) {
            score += 5.0;
        }

        // Content quality indicators (20 points)
        if (hasQuantifiableAchievements(rawText)) score += 8.0;
        if (hasActionVerbs(rawText)) score += 6.0;
        if (hasProfessionalTone(rawText)) score += 4.0;
        if (!containsPersonalPronouns(rawText)) score += 2.0;

        return Math.min(maxScore, score);
    }

    private double calculateContentQualityScore(ParseResultDto parse) {
        // Reuse the content richness calculation
        return calculateContentRichnessScore(parse);
    }

    private int extractExperienceYears(String rawText) {
        // Extract years of experience from text using regex patterns
        String[] patterns = {
                "(\\d+)\\+?\\s*years?\\s*(of\\s*)?experience",
                "(\\d+)\\+?\\s*yrs?\\s*(of\\s*)?experience",
                "experience.*?(\\d+)\\+?\\s*years?",
                "(\\d+)\\+?\\s*years?.*?experience"
        };

        String lowerText = rawText.toLowerCase();

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(lowerText);
            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    // Continue to next pattern
                }
            }
        }

        // Fallback: estimate based on work experience entries
        return 0; // Default if no experience years found
    }

    private boolean hasCareerProgression(String rawText) {
        String lowerText = rawText.toLowerCase();

        // Look for progression indicators
        String[] progressionKeywords = {
                "promoted", "promotion", "advanced", "led", "managed", "senior",
                "lead", "principal", "architect", "director", "head of"
        };

        return Arrays.stream(progressionKeywords)
                .anyMatch(lowerText::contains);
    }

    // Utility methods for text analysis
    private String categorizeSkill(String skill) {
        Map<String, List<String>> skillCategories = Map.of(
                "backend", List.of("java", "spring", "spring boot", "node", "python", "sql", "database"),
                "frontend", List.of("react", "angular", "javascript", "html", "css", "typescript"),
                "devops", List.of("docker", "kubernetes", "aws", "azure", "jenkins", "git", "ci/cd"),
                "mobile", List.of("android", "ios", "react native", "flutter", "kotlin", "swift"),
                "data", List.of("machine learning", "python", "pandas", "tensorflow", "pytorch", "data")
        );

        for (Map.Entry<String, List<String>> entry : skillCategories.entrySet()) {
            if (entry.getValue().stream().anyMatch(s -> skill.contains(s) || s.contains(skill))) {
                return entry.getKey();
            }
        }
        return "other";
    }

    private boolean hasQuantifiableAchievements(String text) {
        String[] patterns = {
                "\\d+%", "\\d+\\s*(years?|months?)", "\\$\\d+",
                "\\d+\\s*(users?|customers?|projects?)", "\\d+\\s*(team|people)",
                "increased.*?\\d+", "reduced.*?\\d+", "improved.*?\\d+"
        };

        return Arrays.stream(patterns)
                .anyMatch(pattern -> text.matches(".*" + pattern + ".*"));
    }

    private boolean hasActionVerbs(String text) {
        String[] actionVerbs = {
                "developed", "implemented", "created", "led", "managed", "built",
                "designed", "optimized", "improved", "achieved", "delivered",
                "established", "launched", "coordinated", "executed", "maintained"
        };

        String lowerText = text.toLowerCase();
        return Arrays.stream(actionVerbs)
                .anyMatch(lowerText::contains);
    }

    private boolean hasProfessionalTone(String text) {
        String[] professionalTerms = {
                "responsible", "experience", "skills", "projects", "achievements",
                "accomplished", "proficient", "expertise", "collaborated"
        };

        String[] casualTerms = {
                "stuff", "things", "cool", "awesome", "guys", "pretty good"
        };

        String lowerText = text.toLowerCase();

        long professionalCount = Arrays.stream(professionalTerms)
                .mapToLong(term -> countOccurrences(lowerText, term))
                .sum();

        long casualCount = Arrays.stream(casualTerms)
                .mapToLong(term -> countOccurrences(lowerText, term))
                .sum();

        return professionalCount > casualCount;
    }

    private boolean containsPersonalPronouns(String text) {
        String[] pronouns = {" i ", " me ", " my ", " mine ", " myself "};
        String lowerText = " " + text.toLowerCase() + " ";

        return Arrays.stream(pronouns)
                .anyMatch(lowerText::contains);
    }

    private long countOccurrences(String text, String word) {
        if (text == null || word == null || word.isEmpty()) return 0;

        String[] words = text.split("\\s+");
        return Arrays.stream(words)
                .mapToLong(w -> w.equalsIgnoreCase(word) ? 1 : 0)
                .sum();
    }
}
