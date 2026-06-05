package lemuel.com.codingtest.learningpath;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 카테고리별 학습 경로. seed/learning-paths.json 매핑.
 * <p>
 * DB 저장 X — 정적 JSON 로드 + ProblemRepository 로 동적 status 조합.
 */
@Data
@NoArgsConstructor
public class LearningPath {
    private String category;
    private String title;
    private String description;
    private List<Step> steps;

    @Data
    @NoArgsConstructor
    public static class Step {
        private String title;
        private String note;
    }
}
