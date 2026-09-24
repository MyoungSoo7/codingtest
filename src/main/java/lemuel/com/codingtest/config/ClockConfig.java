package lemuel.com.codingtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** "오늘" 은 KST 기준 — 파드 시간대(UTC)와 무관하게. 테스트는 이 빈을 고정 시계로 바꾼다. */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
