package lemuel.com.codingtest.solution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/problems/{problemId}/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    @GetMapping("/new")
    public String createForm(@PathVariable Long problemId, Model model) {
        model.addAttribute("problemId", problemId);
        model.addAttribute("languages", Language.values());
        return "solution/form";
    }

    @PostMapping
    public String create(@PathVariable Long problemId,
                          @RequestParam String code,
                          @RequestParam Language language,
                          @RequestParam(required = false) String note) {
        solutionService.create(problemId, code, language, note);
        return "redirect:/problems/" + problemId;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long problemId, @PathVariable Long id, Model model) {
        model.addAttribute("solution", solutionService.findById(id));
        model.addAttribute("problemId", problemId);
        model.addAttribute("languages", Language.values());
        return "solution/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long problemId, @PathVariable Long id,
                          @RequestParam String code,
                          @RequestParam Language language,
                          @RequestParam(required = false) String note) {
        solutionService.update(id, code, language, note);
        return "redirect:/problems/" + problemId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long problemId, @PathVariable Long id) {
        solutionService.delete(id);
        return "redirect:/problems/" + problemId;
    }
}
