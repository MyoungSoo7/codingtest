package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryService;
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
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void listProblems() throws Exception {
        mockMvc.perform(get("/problems"))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/list"))
            .andExpect(model().attributeExists("problems"));
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/problems/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/form"))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    void createProblem() throws Exception {
        Category dp = categoryService.create("DP", "");

        mockMvc.perform(post("/problems")
                .param("title", "피보나치 수")
                .param("sourceUrl", "https://boj.kr/1003")
                .param("difficulty", "EASY")
                .param("categoryIds", dp.getId().toString()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void showDetail() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/problems/" + p.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/detail"))
            .andExpect(model().attributeExists("problem"));
    }

    @Test
    void updateStatus() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/status")
                .param("status", "SOLVED"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteProblem() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems"));
    }
}
