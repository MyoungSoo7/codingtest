package lemuel.com.codingtest.seed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryRepository;
import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 기동 시 problems / categories 가 비어있으면 seed JSON 으로 초기 데이터 로드.
 * <br>
 * - seed/categories.json — 카테고리 16 개 (배열/문자열, DP, 그래프, ...)
 * - seed/problems.json   — LeetCode + 프로그래머스 메타데이터 (제목/난이도/카테고리/외부 URL)
 * <br>
 * 저작권: 문제 본문은 포함하지 않음. 제목 + sourceUrl 만 (메타데이터).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDataLoader implements CommandLineRunner {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (problemRepository.count() > 0) {
            log.info("Seed skip — problemRepository.count() = {}", problemRepository.count());
            return;
        }

        Map<String, Category> categoryMap = loadCategories();
        int loaded = loadProblems(categoryMap);

        log.info("Seed loaded — categories={}, problems={}", categoryMap.size(), loaded);
    }

    private Map<String, Category> loadCategories() throws Exception {
        Map<String, Category> map = new HashMap<>();
        try (InputStream is = new ClassPathResource("seed/categories.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            for (JsonNode node : root.get("categories")) {
                String name = node.get("name").asText();
                String description = node.has("description") ? node.get("description").asText() : null;
                Category category = categoryRepository.findByName(name)
                    .orElseGet(() -> categoryRepository.save(new Category(name, description)));
                map.put(name, category);
            }
        }
        return map;
    }

    private int loadProblems(Map<String, Category> categoryMap) throws Exception {
        int count = 0;
        try (InputStream is = new ClassPathResource("seed/problems.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            for (JsonNode node : root.get("problems")) {
                Problem problem = new Problem();
                problem.setTitle(node.get("title").asText());
                problem.setSourceUrl(node.has("sourceUrl") ? node.get("sourceUrl").asText() : null);
                problem.setDifficulty(parseDifficulty(node));

                Set<Category> categories = new HashSet<>();
                if (node.has("categories")) {
                    for (JsonNode catName : node.get("categories")) {
                        Category cat = categoryMap.get(catName.asText());
                        if (cat != null) {
                            categories.add(cat);
                        } else {
                            log.warn("Unknown category in seed: {} (problem: {})",
                                catName.asText(), problem.getTitle());
                        }
                    }
                }
                problem.setCategories(categories);

                problemRepository.save(problem);
                count++;
            }
        }
        return count;
    }

    private Difficulty parseDifficulty(JsonNode node) {
        if (!node.has("difficulty")) return null;
        try {
            return Difficulty.valueOf(node.get("difficulty").asText().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
