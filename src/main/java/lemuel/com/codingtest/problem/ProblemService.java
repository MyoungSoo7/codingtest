package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;

    public List<Problem> findAll() {
        return problemRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Problem findById(Long id) {
        return problemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));
    }

    public List<Problem> findRecent() {
        return problemRepository.findTop5ByOrderByUpdatedAtDesc();
    }

    public long count() {
        return problemRepository.count();
    }

    public long countByStatus(SolveStatus status) {
        return problemRepository.countByStatus(status);
    }

    public List<Map<String, Object>> getCategoryProgress() {
        return categoryRepository.findCategoryProgress().stream().map(row -> {
            String name = (String) row[0];
            long catTotal = (Long) row[1];
            long catSolved = (Long) row[2];
            return Map.<String, Object>of(
                "name", name,
                "total", catTotal,
                "solved", catSolved,
                "percent", catTotal > 0 ? (int)(catSolved * 100 / catTotal) : 0
            );
        }).toList();
    }

    public List<Problem> findByFilters(String keyword, Long categoryId, Difficulty difficulty, SolveStatus status, String sort) {
        List<Problem> results = new java.util.ArrayList<>(problemRepository.findByFilters(
            keyword != null && keyword.isBlank() ? null : keyword,
            categoryId, difficulty, status));
        Comparator<Problem> comparator = switch (sort) {
            case "title" -> Comparator.comparing(Problem::getTitle);
            case "difficulty" -> Comparator.comparing(Problem::getDifficulty);
            case "status" -> Comparator.comparing(Problem::getStatus);
            default -> Comparator.comparing(Problem::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        results.sort(comparator);
        return results;
    }

    @Transactional
    public Problem create(String title, String sourceUrl, Difficulty difficulty, Set<Long> categoryIds) {
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setSourceUrl(sourceUrl);
        problem.setDifficulty(difficulty);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            problem.setCategories(categories);
        }
        return problemRepository.save(problem);
    }

    @Transactional
    public void update(Long id, String title, String sourceUrl, Difficulty difficulty, Set<Long> categoryIds) {
        Problem problem = findById(id);
        problem.setTitle(title);
        problem.setSourceUrl(sourceUrl);
        problem.setDifficulty(difficulty);
        problem.getCategories().clear();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            problem.setCategories(categories);
        }
    }

    @Transactional
    public void updateStatus(Long id, SolveStatus status) {
        Problem problem = findById(id);
        problem.setStatus(status);
    }

    @Transactional
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }
}
