package lemuel.com.codingtest.daily;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 운영과 같이 테스트 트랜잭션 없이 부른다. @Transactional 테스트는 세션을 열어 둬서
 * 카테고리 지연 로딩 실패(LazyInitializationException → 500)를 가렸다.
 * 시드 데이터(카테고리 있음)를 쓰도록 별도 인메모리 DB.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:daily-notx;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class DailyApiNoTxTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void todayReturnsCategoriesOutsideTransaction() throws Exception {
        mockMvc.perform(get("/api/daily/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categories").isArray())
            .andExpect(jsonPath("$.categories[0]").isString());
        mockMvc.perform(get("/api/daily/today"))
            .andExpect(status().isOk());
    }
}
