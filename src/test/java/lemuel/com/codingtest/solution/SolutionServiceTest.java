package lemuel.com.codingtest.solution;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SolutionServiceTest {

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private ProblemService problemService;

    @Test
    void createSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "System.out.println(1);", Language.JAVA, "brute force approach");

        assertThat(solution.getId()).isNotNull();
        assertThat(solution.getCode()).isEqualTo("System.out.println(1);");
        assertThat(solution.getLanguage()).isEqualTo(Language.JAVA);
        assertThat(solution.getNote()).isEqualTo("brute force approach");
    }

    @Test
    void updateSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "old code", Language.JAVA, "old note");

        solutionService.update(solution.getId(), "new code", Language.PYTHON, "new note");

        Solution updated = solutionService.findById(solution.getId());
        assertThat(updated.getCode()).isEqualTo("new code");
        assertThat(updated.getLanguage()).isEqualTo(Language.PYTHON);
        assertThat(updated.getNote()).isEqualTo("new note");
    }

    @Test
    void deleteSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "code", Language.JAVA, "");

        solutionService.delete(solution.getId());

        Problem reloaded = problemService.findById(problem.getId());
        assertThat(reloaded.getSolutions()).isEmpty();
    }
}
