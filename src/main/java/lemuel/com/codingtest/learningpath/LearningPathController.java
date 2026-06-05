package lemuel.com.codingtest.learningpath;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("paths", learningPathService.findAll());
        return "learning-path/list";
    }

    @GetMapping("/{category}")
    public String detail(@PathVariable String category, Model model, RedirectAttributes ra) {
        String decoded = URLDecoder.decode(category, StandardCharsets.UTF_8);
        var pathOpt = learningPathService.findByCategory(decoded);
        if (pathOpt.isEmpty()) {
            ra.addFlashAttribute("error", "학습 경로를 찾을 수 없습니다: " + decoded);
            return "redirect:/learning-paths";
        }
        var path = pathOpt.get();
        model.addAttribute("path", path);
        model.addAttribute("resolvedSteps", learningPathService.resolve(path));
        return "learning-path/detail";
    }
}
