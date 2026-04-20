package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createProblem() {
        Category dp = categoryService.create("DP", "");
        Problem problem = problemService.create("피보나치 수", "https://boj.kr/1003", Difficulty.EASY, Set.of(dp.getId()));

        assertThat(problem.getId()).isNotNull();
        assertThat(problem.getTitle()).isEqualTo("피보나치 수");
        assertThat(problem.getStatus()).isEqualTo(SolveStatus.NOT_ATTEMPTED);
        assertThat(problem.getCategories()).hasSize(1);
    }

    @Test
    void findAllOrderByUpdatedAtDesc() {
        problemService.create("A", null, Difficulty.EASY, Set.of());
        problemService.create("B", null, Difficulty.MEDIUM, Set.of());

        List<Problem> all = problemService.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void updateStatus() {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        problemService.updateStatus(p.getId(), SolveStatus.SOLVED);

        Problem updated = problemService.findById(p.getId());
        assertThat(updated.getStatus()).isEqualTo(SolveStatus.SOLVED);
    }

    @Test
    void deleteProblem() {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        problemService.delete(p.getId());

        List<Problem> all = problemService.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void countByStatus() {
        problemService.create("A", null, Difficulty.EASY, Set.of());
        Problem b = problemService.create("B", null, Difficulty.MEDIUM, Set.of());
        problemService.updateStatus(b.getId(), SolveStatus.SOLVED);

        assertThat(problemService.countByStatus(SolveStatus.NOT_ATTEMPTED)).isEqualTo(1);
        assertThat(problemService.countByStatus(SolveStatus.SOLVED)).isEqualTo(1);
    }

    @Test
    void findRecent() {
        for (int i = 0; i < 10; i++) {
            problemService.create("Problem " + i, null, Difficulty.EASY, Set.of());
        }

        List<Problem> recent = problemService.findRecent();
        assertThat(recent).hasSize(5);
    }
}
