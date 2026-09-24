package lemuel.com.codingtest.daily;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.problem.Problem;

import java.util.List;

/**
 * API 응답. 카테고리는 지연 로딩이라 반드시 트랜잭션 안에서 만든다(open-in-view=false).
 */
public record DailyView(String date, Long problemId, String title, String difficulty, String sourceUrl,
                        List<String> categories, String hint, Long solutionId) {

    static DailyView of(DailyPick d) {
        Problem p = d.getProblem();
        return new DailyView(d.getPickDate().toString(), p.getId(), p.getTitle(),
            p.getDifficulty() == null ? null : p.getDifficulty().name(), p.getSourceUrl(),
            p.getCategories().stream().map(Category::getName).sorted().toList(),
            d.getHint(), d.getSolutionId());
    }
}
