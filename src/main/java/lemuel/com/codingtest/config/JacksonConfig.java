package lemuel.com.codingtest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 modular 구조에서 starter-webmvc / starter-json 만으론
 * ObjectMapper auto-configuration 이 활성화 안 됨.
 * 명시적 @Bean 으로 등록 — SeedDataLoader / LearningPathService 에서 주입.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
