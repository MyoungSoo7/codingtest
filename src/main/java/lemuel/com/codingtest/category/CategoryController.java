package lemuel.com.codingtest.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categoriesWithCount", categoryService.findAllWithProblemCount());
        return "category/list";
    }

    @PostMapping
    public String create(@RequestParam String name, @RequestParam(required = false) String description) {
        categoryService.create(name, description);
        return "redirect:/categories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String name, @RequestParam(required = false) String description) {
        categoryService.update(id, name, description);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
