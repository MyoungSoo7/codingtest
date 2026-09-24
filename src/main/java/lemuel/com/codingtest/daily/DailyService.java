package lemuel.com.codingtest.daily;

import lemuel.com.codingtest.learningpath.LearningPath;
import lemuel.com.codingtest.learningpath.LearningPathService;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemRepository;
import lemuel.com.codingtest.problem.SolveStatus;
import lemuel.com.codingtest.solution.Language;
import lemuel.com.codingtest.solution.Solution;
import lemuel.com.codingtest.solution.SolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

/**
 * 매일 한 문제 — 학습 경로 순서(경로 파일 순 → step 순), 그다음 경로 밖 문제는 id 순.
 * 이미 뽑힌 문제와 SOLVED 문제는 건너뛴다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyService {

    private final DailyPickRepository dailyPickRepository;
    private final ProblemRepository problemRepository;
    private final LearningPathService learningPathService;
    private final SolutionService solutionService;
    private final Clock clock;

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    public Optional<DailyPick> find(LocalDate date) {
        return dailyPickRepository.findByPickDate(date);
    }

    /** 그 날짜의 문제를 돌려준다. 없으면 다음 문제를 뽑아 고정한다. 남은 문제가 없으면 empty. */
    @Transactional
    public Optional<DailyPick> pick(LocalDate date) {
        Optional<DailyPick> existing = dailyPickRepository.findByPickDate(date);
        if (existing.isPresent()) return existing;

        Set<Long> picked = dailyPickRepository.findPickedProblemIds();
        return nextCandidate(picked).map(c -> {
            DailyPick p = new DailyPick();
            p.setPickDate(date);
            p.setProblem(c.problem());
            p.setHint(c.hint());
            return dailyPickRepository.save(p);
        });
    }

    /**
     * 그 날짜 문제에 풀이를 저장한다. 이미 저장돼 있으면 새로 만들지 않고 기존 것을 돌려준다(멱등).
     */
    @Transactional
    public SaveResult saveSolution(LocalDate date, String code, Language language, String note) {
        DailyPick pick = dailyPickRepository.findByPickDate(date)
            .orElseThrow(() -> new NoSuchElementException("No daily pick for " + date));
        if (pick.getSolutionId() != null) {
            return new SaveResult(pick.getSolutionId(), false);
        }
        Solution s = solutionService.create(pick.getProblem().getId(), code, language, note);
        pick.setSolutionId(s.getId());
        return new SaveResult(s.getId(), true);
    }

    public record SaveResult(Long solutionId, boolean created) {}

    record Candidate(Problem problem, String hint) {}

    private Optional<Candidate> nextCandidate(Set<Long> picked) {
        List<Problem> all = problemRepository.findAll();
        Map<String, Problem> byTitle = new HashMap<>();
        for (Problem p : all) byTitle.putIfAbsent(p.getTitle(), p);

        for (LearningPath path : learningPathService.findAll()) {
            if (path.getSteps() == null) continue;
            for (LearningPath.Step step : path.getSteps()) {
                Problem p = byTitle.get(step.getTitle());
                if (eligible(p, picked)) return Optional.of(new Candidate(p, step.getNote()));
            }
        }
        return all.stream()
            .filter(p -> eligible(p, picked))
            .min(Comparator.comparing(Problem::getId))
            .map(p -> new Candidate(p, null));
    }

    private static boolean eligible(Problem p, Set<Long> picked) {
        return p != null && !picked.contains(p.getId()) && p.getStatus() != SolveStatus.SOLVED;
    }
}
