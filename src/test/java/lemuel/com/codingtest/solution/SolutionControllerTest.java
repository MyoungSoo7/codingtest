package lemuel.com.codingtest.solution;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SolutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SolutionService solutionService;

    @Test
    void showCreateForm() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/problems/" + p.getId() + "/solutions/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("solution/form"))
            .andExpect(model().attributeExists("problemId", "languages"));
    }

    @Test
    void createSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions")
                .param("code", "System.out.println(1);")
                .param("language", "JAVA")
                .param("note", "brute force"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }

    @Test
    void showEditForm() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "code", Language.JAVA, "note");

        mockMvc.perform(get("/problems/" + p.getId() + "/solutions/" + s.getId() + "/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("solution/form"))
            .andExpect(model().attributeExists("solution"));
    }

    @Test
    void updateSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "old", Language.JAVA, "");

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions/" + s.getId())
                .param("code", "new code")
                .param("language", "PYTHON")
                .param("note", "updated"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }

    @Test
    void deleteSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "code", Language.JAVA, "");

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions/" + s.getId() + "/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }
}
