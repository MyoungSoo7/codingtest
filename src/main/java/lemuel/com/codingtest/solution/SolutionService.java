package lemuel.com.codingtest.solution;

import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final ProblemRepository problemRepository;

    public Solution findById(Long id) {
        return solutionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solution not found: " + id));
    }

    @Transactional
    public Solution create(Long problemId, String code, Language language, String note) {
        Problem problem = problemRepository.findById(problemId)
            .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + problemId));
        Solution solution = new Solution();
        solution.setProblem(problem);
        solution.setCode(code);
        solution.setLanguage(language);
        solution.setNote(note);
        return solutionRepository.save(solution);
    }

    @Transactional
    public void update(Long id, String code, Language language, String note) {
        Solution solution = findById(id);
        solution.setCode(code);
        solution.setLanguage(language);
        solution.setNote(note);
    }

    @Transactional
    public void delete(Long id) {
        solutionRepository.deleteById(id);
    }
}
