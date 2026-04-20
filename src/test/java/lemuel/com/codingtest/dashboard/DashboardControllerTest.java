package lemuel.com.codingtest.dashboard;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Test
    void showDashboard() throws Exception {
        problemService.create("Test Problem", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("dashboard"))
            .andExpect(model().attributeExists("totalCount", "solvedCount", "failedCount", "retryCount", "notAttemptedCount", "recentProblems", "categoryProgress"));
    }
}
