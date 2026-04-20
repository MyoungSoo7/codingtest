package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) Difficulty difficulty,
                        @RequestParam(required = false) SolveStatus status,
                        @RequestParam(required = false, defaultValue = "updatedAt") String sort,
                        Model model) {
        model.addAttribute("problems", problemService.findByFilters(keyword, categoryId, difficulty, status, sort));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("statuses", SolveStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        return "problem/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("difficulties", Difficulty.values());
        return "problem/form";
    }

    @PostMapping
    public String create(@RequestParam String title,
                          @RequestParam(required = false) String sourceUrl,
                          @RequestParam Difficulty difficulty,
                          @RequestParam(required = false) Set<Long> categoryIds) {
        Problem problem = problemService.create(title, sourceUrl, difficulty, categoryIds);
        return "redirect:/problems/" + problem.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("problem", problemService.findById(id));
        model.addAttribute("statuses", SolveStatus.values());
        return "problem/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("problem", problemService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("difficulties", Difficulty.values());
        return "problem/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam(required = false) String sourceUrl,
                          @RequestParam Difficulty difficulty,
                          @RequestParam(required = false) Set<Long> categoryIds) {
        problemService.update(id, title, sourceUrl, difficulty, categoryIds);
        return "redirect:/problems/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam SolveStatus status) {
        problemService.updateStatus(id, status);
        return "redirect:/problems/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        problemService.delete(id);
        return "redirect:/problems";
    }
}
