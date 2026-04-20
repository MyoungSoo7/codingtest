package lemuel.com.codingtest.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Test
    void listCategories() throws Exception {
        categoryService.create("DP", "Dynamic Programming");

        mockMvc.perform(get("/categories"))
            .andExpect(status().isOk())
            .andExpect(view().name("category/list"))
            .andExpect(model().attributeExists("categoriesWithCount"));
    }

    @Test
    void createCategory() throws Exception {
        mockMvc.perform(post("/categories")
                .param("name", "Graph")
                .param("description", "Graph algorithms"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/categories"));
    }

    @Test
    void deleteCategory() throws Exception {
        Category cat = categoryService.create("Temp", "");

        mockMvc.perform(post("/categories/" + cat.getId() + "/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/categories"));
    }
}
