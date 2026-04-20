package lemuel.com.codingtest.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    public List<Map<String, Object>> findAllWithProblemCount() {
        return categoryRepository.findAllWithProblemCount().stream()
            .map(row -> Map.<String, Object>of("category", (Category) row[0], "problemCount", (Long) row[1]))
            .toList();
    }

    @Transactional
    public Category create(String name, String description) {
        return categoryRepository.save(new Category(name, description));
    }

    @Transactional
    public void update(Long id, String name, String description) {
        Category category = findById(id);
        category.setName(name);
        category.setDescription(description);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
