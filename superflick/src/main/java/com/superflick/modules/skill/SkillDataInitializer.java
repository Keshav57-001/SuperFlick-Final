package com.superflick.modules.skill;

import com.superflick.modules.skill.entity.Skill;
import com.superflick.modules.skill.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Seeds the skills master table on startup if it is empty.
 * Uses fixed UUID5-derived IDs so they never change across restarts or DB wipes.
 * No data.sql, no spring.sql.init.mode — just runs via @PostConstruct.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SkillDataInitializer {

    private final SkillRepository skillRepository;

    private record SkillSeed(String id, String name, String category) {}

    private static final List<SkillSeed> SKILLS = List.of(
        // Programming Languages
        new SkillSeed("39285cee-93ab-545d-8e60-812c488a6560", "Java",           "Programming Languages"),
        new SkillSeed("92224eeb-536c-5ec8-820c-6618419cdc2b", "Python",         "Programming Languages"),
        new SkillSeed("c6aa2682-87bf-5257-a013-09b6c63c182f", "JavaScript",     "Programming Languages"),
        new SkillSeed("100c65f7-39d6-549a-8179-a181a2fdb023", "TypeScript",     "Programming Languages"),
        new SkillSeed("4ff53e5e-d0e0-53fa-a49d-3d79ae5d78b3", "Go",             "Programming Languages"),
        new SkillSeed("2aa823fe-4e18-502b-b87b-706e1b5a7318", "Kotlin",         "Programming Languages"),
        new SkillSeed("fa623bc6-c1ef-5e9f-a410-50f8299f52bf", "Rust",           "Programming Languages"),
        new SkillSeed("fa88f987-c65b-55ed-a21f-3141d4026830", "C++",            "Programming Languages"),
        new SkillSeed("5fe55a21-b279-59bc-abe1-547cbc45d0df", "C#",             "Programming Languages"),
        new SkillSeed("9a937896-d3f9-5925-8ec7-86456dd9514b", "PHP",            "Programming Languages"),
        new SkillSeed("8f1c51a0-7ce9-5a56-9275-327b5f02bdbb", "Ruby",           "Programming Languages"),
        new SkillSeed("93a4f53d-5c3c-5d95-86cc-c864a52d7df3", "Scala",          "Programming Languages"),
        new SkillSeed("0f915c5d-3100-5791-b84c-c490f2206020", "Dart",           "Programming Languages"),
        // Frontend
        new SkillSeed("b54ed67b-eaee-585e-88ec-b8ba32af0abe", "HTML5",          "Frontend"),
        new SkillSeed("af1c1850-ca1a-5668-95c7-0e8f0e360e04", "CSS3",           "Frontend"),
        new SkillSeed("7dcf9c14-5ff2-5473-a144-fac30224fbaf", "React",          "Frontend"),
        new SkillSeed("62755a0b-f643-5e5f-a8c7-a16f92eabf5d", "Next.js",        "Frontend"),
        new SkillSeed("950a3819-f886-5096-8bd1-94d9706c7a24", "Vue.js",         "Frontend"),
        new SkillSeed("da941e9b-a56b-5e58-be53-d9b91f04b08a", "Angular",        "Frontend"),
        new SkillSeed("5f0d1372-b6a0-5c15-ab98-f95aca1563f7", "Tailwind CSS",   "Frontend"),
        new SkillSeed("85c370fa-ca9a-5904-bc53-b944a01fa282", "Redux",          "Frontend"),
        new SkillSeed("14a6ccd4-1c5b-5a23-a928-85339545864a", "GraphQL",        "Frontend"),
        // Backend
        new SkillSeed("2fb80cbf-a362-513c-b7ab-9a195d891f3e", "Spring Boot",    "Backend"),
        new SkillSeed("d1ab5a56-f366-58cf-b3c1-6acc4d6e520a", "Node.js",        "Backend"),
        new SkillSeed("299ce46f-86a7-54b5-80ab-87b37ef2337d", "Express.js",     "Backend"),
        new SkillSeed("f975ed7a-b74d-5ed4-8a9f-2b22b7569080", "Django",         "Backend"),
        new SkillSeed("325ad06f-d045-5a47-b1b5-057592262dde", "FastAPI",        "Backend"),
        new SkillSeed("cca49402-a1e4-5477-84bf-6d060ed0db0c", "Flask",          "Backend"),
        new SkillSeed("72321ca1-20f3-5b22-a34c-28aff6cd15c7", "Laravel",        "Backend"),
        new SkillSeed("ae30ce89-40a6-5b26-88fc-307c09afe677", "NestJS",         "Backend"),
        new SkillSeed("6c4c2f8d-77e1-5287-998b-eebf243cbe41", "REST API",       "Backend"),
        // Mobile
        new SkillSeed("ba1820e5-18e5-5855-b00a-9a89ce17d08d", "Android",        "Mobile"),
        new SkillSeed("948e6d4a-3714-5864-932d-1bc2818356e5", "iOS",            "Mobile"),
        new SkillSeed("87cc4db6-2138-50e2-9dba-8ee00ef5085c", "React Native",   "Mobile"),
        new SkillSeed("f99c32c7-cb71-5f90-b8eb-f45ae409b260", "Flutter",        "Mobile"),
        new SkillSeed("b9cf1209-c475-5ded-a4d0-ad62fbf4607d", "SwiftUI",        "Mobile"),
        new SkillSeed("23d7a445-b43c-5632-a135-516a2bca12de", "Jetpack Compose","Mobile"),
        // Databases
        new SkillSeed("4b1b127b-d42d-5c75-a40e-1847d6c75954", "MySQL",          "Databases"),
        new SkillSeed("96d3a68d-1387-5f75-b737-f9ef586b0096", "PostgreSQL",     "Databases"),
        new SkillSeed("f92df036-5ab2-5871-8a12-ea57c60bcecc", "MongoDB",        "Databases"),
        new SkillSeed("3833a8e6-2838-500a-91d7-6214b26a2af3", "Redis",          "Databases"),
        new SkillSeed("8f0c4f26-d01c-5ded-ab5c-c054671f3c82", "Elasticsearch",  "Databases"),
        new SkillSeed("7eb56789-c5a5-5e53-add1-7f88d4ced7b3", "Firebase",       "Databases"),
        new SkillSeed("d0bfc0e3-401f-580b-be14-dc1bbe995478", "DynamoDB",       "Databases"),
        new SkillSeed("551744dc-146d-5614-89e0-171337ecc194", "SQLite",         "Databases"),
        // Cloud & DevOps
        new SkillSeed("1cdf6393-c0a7-5d2c-a06c-7a2ba53fef79", "AWS",            "Cloud & DevOps"),
        new SkillSeed("27bd0cc5-aad1-55de-bad6-8e43e5a0f8fc", "Google Cloud",   "Cloud & DevOps"),
        new SkillSeed("533a27b5-1242-500f-b982-47fa3b60ece8", "Azure",          "Cloud & DevOps"),
        new SkillSeed("72f2897d-c981-53eb-bb31-e5dbeaab4cf3", "Docker",         "Cloud & DevOps"),
        new SkillSeed("a5186989-025d-50bb-8ce9-70a0339880a9", "Kubernetes",     "Cloud & DevOps"),
        new SkillSeed("e9a12624-8dee-5690-adbc-ea9440388d05", "Terraform",      "Cloud & DevOps"),
        new SkillSeed("4d98ffd6-bd7c-5d16-b3b2-768199ac4f5b", "CI/CD",          "Cloud & DevOps"),
        new SkillSeed("c38c883a-81cc-5e98-9cad-317d6b43e7f7", "GitHub Actions", "Cloud & DevOps"),
        new SkillSeed("0ef23e7e-f3fb-5d74-bfac-a287a67e38d7", "Linux",          "Cloud & DevOps"),
        // Data & AI
        new SkillSeed("54591b2b-3dda-52c2-8416-18a4350d4c63", "Machine Learning","Data & AI"),
        new SkillSeed("5d9acaff-833e-5098-a00a-f8273f535bbc", "Deep Learning",  "Data & AI"),
        new SkillSeed("b24dbc9e-2d4f-56d9-be42-a77b2e9a4742", "TensorFlow",     "Data & AI"),
        new SkillSeed("6e041387-cd53-51e6-a276-c0608aaefbea", "PyTorch",        "Data & AI"),
        new SkillSeed("af0dc6e9-0e82-50b2-b63e-c443be6d3e27", "Pandas",         "Data & AI"),
        new SkillSeed("435d8138-d027-567c-a01c-bb7812cf6a75", "NumPy",          "Data & AI"),
        new SkillSeed("59434aff-10ca-5669-89d2-cb622293d739", "Data Analysis",  "Data & AI"),
        new SkillSeed("47a36f0b-3e23-5171-b84d-56c717399f22", "LLMs",           "Data & AI"),
        new SkillSeed("e4117e01-9544-5cb3-9c1c-7c9a8cd65c52", "NLP",            "Data & AI"),
        new SkillSeed("9e93bc12-4a68-5fe5-8179-7c6f269ff4a1", "Computer Vision","Data & AI"),
        // Testing
        new SkillSeed("172ec5a6-09f2-5f05-bae0-fe58d6237913", "Jest",           "Testing"),
        new SkillSeed("6bfdfe0a-e117-53ff-be8a-a742241b9314", "Cypress",        "Testing"),
        new SkillSeed("5ae43bbb-b4bc-5c9e-915d-52a75b1bca61", "Postman",        "Testing"),
        new SkillSeed("845b7c3a-6996-514e-ac2c-d21e436771b8", "Selenium",       "Testing"),
        new SkillSeed("b53a8767-5598-570a-96b1-7c3ad4c24d11", "Playwright",     "Testing"),
        // Design
        new SkillSeed("4d8bae8e-79ff-5e9c-94c0-8536320b8b0c", "Figma",          "Design"),
        new SkillSeed("c0d0e5d6-8147-5f2d-8ed7-9653025b0d00", "Adobe XD",       "Design"),
        new SkillSeed("1d70712a-d1f2-56f4-8d2c-f0df8cd34ab6", "UI/UX Design",   "Design"),
        // Soft Skills
        new SkillSeed("6ef5f251-2929-5f5a-94b9-d0ce68a75031", "Agile",          "Soft Skills"),
        new SkillSeed("4b108d76-a9d3-537e-979c-febf2f4db90f", "Scrum",          "Soft Skills"),
        new SkillSeed("cf735096-2398-52ba-af07-d5947221c94e", "System Design",  "Soft Skills"),
        new SkillSeed("dc81eef2-4593-5c27-ae0a-1edc0e29b3d2", "Leadership",     "Soft Skills"),
        new SkillSeed("0bc16761-62c9-57a8-b9bb-e847e160f54b", "Product Management","Soft Skills"),
        new SkillSeed("4a4061f5-5fc8-5dcc-933d-fb942ec3f5fd", "Project Management","Soft Skills"),
        // Domain
        new SkillSeed("38545fe5-2000-5ce6-9188-6d686031d400", "Fintech",        "Domain"),
        new SkillSeed("d061a227-53ac-5d59-aa5b-6b59f655d400", "EdTech",         "Domain"),
        new SkillSeed("2d77a8d9-a4c2-5216-852c-bbaeb08f033f", "E-commerce",     "Domain"),
        new SkillSeed("81709c72-016e-5d63-984d-a1b829547387", "Blockchain",     "Domain"),
        new SkillSeed("a3d43b06-5d77-5ac8-8e4d-087e7eaa3cbf", "Cybersecurity",  "Domain")
    );

    @PostConstruct
    @Transactional
    public void seed() {
        long existing = skillRepository.count();
        if (existing > 0) {
            log.info("Skills already seeded ({} records) — skipping", existing);
            return;
        }

        log.info("Seeding {} skills...", SKILLS.size());
        List<Skill> entities = SKILLS.stream()
            .map(s -> Skill.builder()
                .id(UUID.fromString(s.id()))
                .name(s.name())
                .category(s.category())
                .build())
            .toList();

        skillRepository.saveAll(entities);
        log.info("✅ Skills seeded successfully: {} records", SKILLS.size());
    }
}
