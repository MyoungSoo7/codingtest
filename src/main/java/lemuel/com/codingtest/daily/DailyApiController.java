package lemuel.com.codingtest.daily;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.solution.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * n8n "매일 문제·풀이" 용 JSON API.
 * <p>
 * 조회는 공개(화면과 같은 데이터). 저장은 {@code X-Api-Key} 가 {@code codingtest.api-key} 와 같아야 한다.
 * 키가 설정돼 있지 않으면 저장은 늘 503 — 키 없이 열리지 않게(fail-closed).
 */
@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyApiController {

    private final DailyService dailyService;

    @Value("${codingtest.api-key:}")
    private String apiKey;

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

    public record SolutionRequest(String code, Language language, String note) {}

    /** 오늘(KST) 문제. 처음 부르면 뽑아서 고정한다. 남은 문제가 없으면 404. */
    @GetMapping("/today")
    public ResponseEntity<?> today() {
        return dailyService.pick(dailyService.today())
            .<ResponseEntity<?>>map(d -> ResponseEntity.ok(DailyView.of(d)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "no problems left")));
    }

    /** 그 날짜 문제 조회만(뽑지 않음). */
    @GetMapping("/{date}")
    public ResponseEntity<?> byDate(@PathVariable LocalDate date) {
        return dailyService.find(date)
            .<ResponseEntity<?>>map(d -> ResponseEntity.ok(DailyView.of(d)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "no pick for " + date)));
    }

    /** 그 날짜 문제에 풀이 저장. 이미 있으면 200 + 기존 id, 새로 만들면 201. */
    @PostMapping("/{date}/solution")
    public ResponseEntity<?> saveSolution(@PathVariable LocalDate date,
                                          @RequestHeader(value = "X-Api-Key", required = false) String key,
                                          @RequestBody SolutionRequest req) {
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", "api key not configured"));
        }
        if (key == null || !MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8), apiKey.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unauthorized"));
        }
        if (req == null || req.code() == null || req.code().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "code required"));
        }
        try {
            DailyService.SaveResult r = dailyService.saveSolution(date, req.code(),
                req.language() == null ? Language.JAVA : req.language(), req.note());
            return ResponseEntity.status(r.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(Map.of("solutionId", r.solutionId(), "created", r.created()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
