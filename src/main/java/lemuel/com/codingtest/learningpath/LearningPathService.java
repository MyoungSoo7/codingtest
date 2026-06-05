package lemuel.com.codingtest.learningpath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    private List<LearningPath> paths = Collections.emptyList();

    @PostConstruct
    void load() {
        try (InputStream is = new ClassPathResource("seed/learning-paths.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            this.paths = objectMapper.convertValue(
                root.get("paths"),
                new TypeReference<List<LearningPath>>() {}
            );
            log.info("LearningPath loaded — {} paths", paths.size());
        } catch (Exception e) {
            log.warn("Failed to load learning-paths.json — empty list. {}", e.getMessage());
        }
    }

    public List<LearningPath> findAll() {
        return paths;
    }

    public Optional<LearningPath> findByCategory(String category) {
        return paths.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .findFirst();
    }

    /**
     * 학습 경로의 각 step 에 해당하는 Problem 을 *''**title 매칭*** 으로 찾아 반환.
     * Problem 이 없으면 null. 매핑 결과는 {step → Problem?} 의 ordered map.
     */
    public List<ResolvedStep> resolve(LearningPath path) {
        if (path == null || path.getSteps() == null) return List.of();

        Set<String> titles = path.getSteps().stream()
            .map(LearningPath.Step::getTitle)
            .collect(Collectors.toSet());

        Map<String, Problem> byTitle = problemRepository.findAll().stream()
            .filter(p -> titles.contains(p.getTitle()))
            .collect(Collectors.toMap(Problem::getTitle, p -> p, (a, b) -> a));

        return path.getSteps().stream()
            .map(step -> new ResolvedStep(step, byTitle.get(step.getTitle())))
            .toList();
    }

    public record ResolvedStep(LearningPath.Step step, Problem problem) {}
}
