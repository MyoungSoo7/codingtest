package lemuel.com.codingtest.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void createAndFindAll() {
        categoryService.create("DP", "Dynamic Programming");
        categoryService.create("Graph", "Graph algorithms");

        List<Category> all = categoryService.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(Category::getName).containsExactlyInAnyOrder("DP", "Graph");
    }

    @Test
    void update() {
        Category cat = categoryService.create("DP", "");
        categoryService.update(cat.getId(), "Dynamic Programming", "DP problems");

        Category updated = categoryService.findById(cat.getId());
        assertThat(updated.getName()).isEqualTo("Dynamic Programming");
        assertThat(updated.getDescription()).isEqualTo("DP problems");
    }

    @Test
    void delete() {
        Category cat = categoryService.create("Temp", "");
        categoryService.delete(cat.getId());

        List<Category> all = categoryService.findAll();
        assertThat(all).isEmpty();
    }
}
