package lemuel.com.codingtest.dashboard;

import lemuel.com.codingtest.problem.ProblemService;
import lemuel.com.codingtest.problem.SolveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProblemService problemService;

    @GetMapping("/")
    public String dashboard(Model model) {
        long total = problemService.count();
        long solved = problemService.countByStatus(SolveStatus.SOLVED);
        long failed = problemService.countByStatus(SolveStatus.FAILED);
        long retry = problemService.countByStatus(SolveStatus.RETRY);
        long notAttempted = problemService.countByStatus(SolveStatus.NOT_ATTEMPTED);

        model.addAttribute("totalCount", total);
        model.addAttribute("solvedCount", solved);
        model.addAttribute("failedCount", failed);
        model.addAttribute("retryCount", retry);
        model.addAttribute("notAttemptedCount", notAttempted);
        model.addAttribute("recentProblems", problemService.findRecent());
        model.addAttribute("categoryProgress", problemService.getCategoryProgress());

        return "dashboard";
    }
}
