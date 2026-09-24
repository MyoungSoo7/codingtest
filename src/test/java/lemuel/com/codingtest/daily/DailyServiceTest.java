package lemuel.com.codingtest.daily;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.ProblemService;
import lemuel.com.codingtest.solution.Language;
import lemuel.com.codingtest.solution.SolutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DailyServiceTest {

    private static final LocalDate D1 = LocalDate.of(2099, 1, 1);
    private static final LocalDate D2 = LocalDate.of(2099, 1, 2);

    @Autowired
    private DailyService dailyService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SolutionService solutionService;

    @Test
    void sameDateReturnsSamePickAndNextDateMovesOn() {
        problemService.create("Daily Fixture", null, Difficulty.EASY, Set.of());

        DailyPick first = dailyService.pick(D1).orElseThrow();
        DailyPick again = dailyService.pick(D1).orElseThrow();
        DailyPick next = dailyService.pick(D2).orElseThrow();

        assertThat(again.getId()).isEqualTo(first.getId());
        assertThat(next.getProblem().getId()).isNotEqualTo(first.getProblem().getId());
    }

    @Test
    void saveSolutionIsIdempotentPerDate() {
        problemService.create("Daily Fixture", null, Difficulty.EASY, Set.of());
        DailyPick pick = dailyService.pick(D1).orElseThrow();

        DailyService.SaveResult created = dailyService.saveSolution(D1, "class S {}", Language.JAVA, "note");
        DailyService.SaveResult replay = dailyService.saveSolution(D1, "class Other {}", Language.JAVA, "note2");

        assertThat(created.created()).isTrue();
        assertThat(replay.created()).isFalse();
        assertThat(replay.solutionId()).isEqualTo(created.solutionId());
        assertThat(solutionService.findById(created.solutionId()).getProblem().getId())
            .isEqualTo(pick.getProblem().getId());
        assertThat(dailyService.find(D1).orElseThrow().getSolutionId()).isEqualTo(created.solutionId());
    }

    @Test
    void saveSolutionWithoutPickFails() {
        assertThatThrownBy(() -> dailyService.saveSolution(D2, "x", Language.JAVA, null))
            .isInstanceOf(NoSuchElementException.class);
    }
}
