package lemuel.com.codingtest.daily;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "codingtest.api-key=test-key")
@AutoConfigureMockMvc
@Transactional
class DailyApiControllerTest {

    private static final LocalDate D = LocalDate.of(2099, 2, 1);
    private static final String BODY = "{\"code\":\"class S {}\",\"language\":\"JAVA\",\"note\":\"n\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DailyService dailyService;

    @Autowired
    private ProblemService problemService;

    @Test
    void todayReturnsProblemJson() throws Exception {
        problemService.create("Daily Fixture", null, Difficulty.EASY, Set.of());
        mockMvc.perform(get("/api/daily/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.problemId").isNumber())
            .andExpect(jsonPath("$.title").isString());
    }

    @Test
    void saveRequiresApiKey() throws Exception {
        problemService.create("Daily Fixture", null, Difficulty.EASY, Set.of());
        dailyService.pick(D);
        mockMvc.perform(post("/api/daily/" + D + "/solution").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/daily/" + D + "/solution").header("X-Api-Key", "wrong")
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void saveCreatesThenReturnsExisting() throws Exception {
        problemService.create("Daily Fixture", null, Difficulty.EASY, Set.of());
        dailyService.pick(D);
        mockMvc.perform(post("/api/daily/" + D + "/solution").header("X-Api-Key", "test-key")
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.created").value(true));
        mockMvc.perform(post("/api/daily/" + D + "/solution").header("X-Api-Key", "test-key")
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.created").value(false));
        mockMvc.perform(get("/api/daily/" + D))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.solutionId").isNumber());
    }

    @Test
    void saveForUnknownDateIs404() throws Exception {
        mockMvc.perform(post("/api/daily/2099-03-01/solution").header("X-Api-Key", "test-key")
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isNotFound());
    }
}
