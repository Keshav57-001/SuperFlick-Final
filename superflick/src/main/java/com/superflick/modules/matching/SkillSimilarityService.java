package com.superflick.modules.matching;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SkillSimilarityService {

    private static final Map<String, List<String>> RELATED = Map.of(
        "java",          List.of("spring boot", "spring", "hibernate", "jpa", "maven", "gradle"),
        "python",        List.of("django", "flask", "fastapi", "pandas", "numpy", "scipy"),
        "javascript",    List.of("react", "node.js", "angular", "vue.js", "typescript", "next.js"),
        "react",         List.of("javascript", "redux", "next.js", "gatsby"),
        "microservices", List.of("docker", "kubernetes", "api gateway", "kafka", "rabbitmq"),
        "aws",           List.of("s3", "ec2", "lambda", "cloudwatch", "rds", "eks"),
        "sql",           List.of("mysql", "postgresql", "oracle", "jdbc", "hibernate"),
        "devops",        List.of("docker", "kubernetes", "jenkins", "github actions", "terraform"),
        "machine learning", List.of("tensorflow", "pytorch", "scikit-learn", "pandas", "numpy")
    );

    public boolean isRelated(String jobSkill, List<String> candidateSkills) {
        List<String> related = RELATED.getOrDefault(jobSkill.toLowerCase(), List.of());
        return candidateSkills.stream()
            .anyMatch(cs -> related.stream().anyMatch(r -> r.equalsIgnoreCase(cs)));
    }
}
