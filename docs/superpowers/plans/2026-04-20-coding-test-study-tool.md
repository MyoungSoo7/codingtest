# Coding Test Study Tool Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a personal coding test study tool that manages algorithm problems by category with solution tracking, notes, and a progress dashboard.

**Architecture:** Spring Boot 4.0.5 + Thymeleaf server-side rendering with H2 file-mode database. Domain-packaged (category/problem/solution) with JPA entities, Spring Data repositories, service layer, and Thymeleaf controllers. Bootstrap 5 CDN for styling, highlight.js CDN for code syntax highlighting.

**Tech Stack:** Java 25, Spring Boot 4.0.5, Spring Data JPA, Thymeleaf, H2, Bootstrap 5, highlight.js, Gradle

**Spec:** `docs/superpowers/specs/2026-04-20-coding-test-study-tool-design.md`

---

## File Structure

```
src/main/java/lemuel/com/codingtest/
├── CodingtestApplication.java                  (modify: add @EnableJpaAuditing)
├── category/
│   ├── Category.java                           (create: JPA entity)
│   ├── CategoryRepository.java                 (create: Spring Data repo)
│   ├── CategoryService.java                    (create: business logic)
│   └── CategoryController.java                 (create: Thymeleaf controller)
├── problem/
│   ├── Difficulty.java                         (create: enum)
│   ├── SolveStatus.java                        (create: enum)
│   ├── Problem.java                            (create: JPA entity)
│   ├── ProblemRepository.java                  (create: Spring Data repo)
│   ├── ProblemService.java                     (create: business logic)
│   └── ProblemController.java                  (create: Thymeleaf controller)
├── solution/
│   ├── Language.java                           (create: enum)
│   ├── Solution.java                           (create: JPA entity)
│   ├── SolutionRepository.java                 (create: Spring Data repo)
│   ├── SolutionService.java                    (create: business logic)
│   └── SolutionController.java                 (create: Thymeleaf controller)
└── dashboard/
    └── DashboardController.java                (create: dashboard page)

src/main/resources/
├── application.yaml                            (modify: H2 + JPA config)
└── templates/
    ├── layout.html                             (create: common layout with nav)
    ├── dashboard.html                          (create: dashboard page)
    ├── category/
    │   └── list.html                           (create: category management)
    ├── problem/
    │   ├── list.html                           (create: problem list with filters)
    │   ├── detail.html                         (create: problem detail + solutions)
    │   └── form.html                           (create: problem create/edit form)
    └── solution/
        └── form.html                           (create: solution create/edit form)

src/test/java/lemuel/com/codingtest/
├── category/
│   ├── CategoryServiceTest.java                (create: unit tests)
│   └── CategoryControllerTest.java             (create: MVC tests)
├── problem/
│   ├── ProblemServiceTest.java                 (create: unit tests)
│   └── ProblemControllerTest.java              (create: MVC tests)
├── solution/
│   ├── SolutionServiceTest.java                (create: unit tests)
│   └── SolutionControllerTest.java             (create: MVC tests)
└── dashboard/
    └── DashboardControllerTest.java            (create: MVC tests)
```

---

## Chunk 1: Foundation — Configuration, Enums, Entities

### Task 1: Configure application.yaml and enable JPA Auditing

**Files:**
- Modify: `src/main/resources/application.yaml`
- Modify: `src/main/java/lemuel/com/codingtest/CodingtestApplication.java`

- [ ] **Step 1: Update application.yaml with H2 + JPA config**

```yaml
spring:
  application:
    name: codingtest
  datasource:
    url: jdbc:h2:file:./data/codingtest
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: false
```

- [ ] **Step 2: Add @EnableJpaAuditing to CodingtestApplication**

```java
package lemuel.com.codingtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CodingtestApplication {

  public static void main(String[] args) {
    SpringApplication.run(CodingtestApplication.class, args);
  }
}
```

- [ ] **Step 3: Add spring-boot-starter-data-jpa and h2 dependencies to build.gradle.kts**

Add to dependencies block:
```kotlin
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
runtimeOnly("com.h2database:h2")
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/application.yaml src/main/java/lemuel/com/codingtest/CodingtestApplication.java build.gradle.kts
git commit -m "feat: configure H2 file-mode DB, JPA auditing, and Thymeleaf"
```

---

### Task 2: Create enums (Difficulty, SolveStatus, Language)

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/problem/Difficulty.java`
- Create: `src/main/java/lemuel/com/codingtest/problem/SolveStatus.java`
- Create: `src/main/java/lemuel/com/codingtest/solution/Language.java`

- [ ] **Step 1: Create Difficulty enum**

```java
package lemuel.com.codingtest.problem;

public enum Difficulty {
    EASY, MEDIUM, HARD
}
```

- [ ] **Step 2: Create SolveStatus enum**

```java
package lemuel.com.codingtest.problem;

public enum SolveStatus {
    NOT_ATTEMPTED, SOLVED, FAILED, RETRY
}
```

- [ ] **Step 3: Create Language enum**

```java
package lemuel.com.codingtest.solution;

public enum Language {
    JAVA, PYTHON, CPP, JAVASCRIPT
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/problem/Difficulty.java src/main/java/lemuel/com/codingtest/problem/SolveStatus.java src/main/java/lemuel/com/codingtest/solution/Language.java
git commit -m "feat: add Difficulty, SolveStatus, and Language enums"
```

---

### Task 3: Create Category entity and repository

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/category/Category.java`
- Create: `src/main/java/lemuel/com/codingtest/category/CategoryRepository.java`
- Test: `src/test/java/lemuel/com/codingtest/category/CategoryServiceTest.java` (later)

- [ ] **Step 1: Create Category entity**

```java
package lemuel.com.codingtest.category;

import jakarta.persistence.*;
import lemuel.com.codingtest.problem.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Problem> problems = new HashSet<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

- [ ] **Step 2: Create CategoryRepository**

```java
package lemuel.com.codingtest.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN c.problems p GROUP BY c ORDER BY c.name")
    List<Object[]> findAllWithProblemCount();

    @Query("SELECT c.name, COUNT(p), SUM(CASE WHEN p.status = 'SOLVED' THEN 1 ELSE 0 END) FROM Category c LEFT JOIN c.problems p GROUP BY c.name ORDER BY c.name")
    List<Object[]> findCategoryProgress();
}
```

**Note:** The `Category` entity has a `@ManyToMany(mappedBy = "categories")` back-reference to `Problem`, enabling this query.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/category/
git commit -m "feat: add Category entity and repository"
```

---

### Task 4: Create Problem entity and repository

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/problem/Problem.java`
- Create: `src/main/java/lemuel/com/codingtest/problem/ProblemRepository.java`

- [ ] **Step 1: Create Problem entity**

```java
package lemuel.com.codingtest.problem;

import jakarta.persistence.*;
import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.solution.Solution;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolveStatus status = SolveStatus.NOT_ATTEMPTED;

    @ManyToMany
    @JoinTable(
        name = "problem_category",
        joinColumns = @JoinColumn(name = "problem_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Solution> solutions = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: Create ProblemRepository**

```java
package lemuel.com.codingtest.problem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findTop5ByOrderByUpdatedAtDesc();
    List<Problem> findAllByOrderByUpdatedAtDesc();
    long countByStatus(SolveStatus status);
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/problem/
git commit -m "feat: add Problem entity and repository"
```

---

### Task 5: Create Solution entity and repository

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/solution/Solution.java`
- Create: `src/main/java/lemuel/com/codingtest/solution/SolutionRepository.java`

- [ ] **Step 1: Create Solution entity**

```java
package lemuel.com.codingtest.solution;

import jakarta.persistence.*;
import lemuel.com.codingtest.problem.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Lob
    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Lob
    private String note;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: Create SolutionRepository**

```java
package lemuel.com.codingtest.solution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepository extends JpaRepository<Solution, Long> {
}
```

- [ ] **Step 3: Verify application context loads**

Run: `./gradlew test --tests "lemuel.com.codingtest.CodingtestApplicationTests"`
Expected: PASS — context loads with all entities configured.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/solution/
git commit -m "feat: add Solution entity and repository"
```

---

## Chunk 2: Service Layer and Unit Tests

### Task 6: Create CategoryService with tests

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/category/CategoryService.java`
- Create: `src/test/java/lemuel/com/codingtest/category/CategoryServiceTest.java`

- [ ] **Step 1: Write CategoryService tests**

```java
package lemuel.com.codingtest.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void createAndFindAll() {
        categoryService.create("DP", "Dynamic Programming");
        categoryService.create("Graph", "Graph algorithms");

        List<Category> all = categoryService.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(Category::getName).containsExactlyInAnyOrder("DP", "Graph");
    }

    @Test
    void update() {
        Category cat = categoryService.create("DP", "");
        categoryService.update(cat.getId(), "Dynamic Programming", "DP problems");

        Category updated = categoryService.findById(cat.getId());
        assertThat(updated.getName()).isEqualTo("Dynamic Programming");
        assertThat(updated.getDescription()).isEqualTo("DP problems");
    }

    @Test
    void delete() {
        Category cat = categoryService.create("Temp", "");
        categoryService.delete(cat.getId());

        List<Category> all = categoryService.findAll();
        assertThat(all).isEmpty();
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.category.CategoryServiceTest"`
Expected: FAIL — CategoryService does not exist yet.

- [ ] **Step 3: Create CategoryService**

```java
package lemuel.com.codingtest.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Transactional
    public Category create(String name, String description) {
        return categoryRepository.save(new Category(name, description));
    }

    @Transactional
    public void update(Long id, String name, String description) {
        Category category = findById(id);
        category.setName(name);
        category.setDescription(description);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.category.CategoryServiceTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/category/CategoryService.java src/test/java/lemuel/com/codingtest/category/CategoryServiceTest.java
git commit -m "feat: add CategoryService with CRUD operations and tests"
```

---

### Task 7: Create ProblemService with tests

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/problem/ProblemService.java`
- Create: `src/test/java/lemuel/com/codingtest/problem/ProblemServiceTest.java`

- [ ] **Step 1: Write ProblemService tests**

```java
package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProblemServiceTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createProblem() {
        Category dp = categoryService.create("DP", "");
        Problem problem = problemService.create("피보나치 수", "https://boj.kr/1003", Difficulty.EASY, Set.of(dp.getId()));

        assertThat(problem.getId()).isNotNull();
        assertThat(problem.getTitle()).isEqualTo("피보나치 수");
        assertThat(problem.getStatus()).isEqualTo(SolveStatus.NOT_ATTEMPTED);
        assertThat(problem.getCategories()).hasSize(1);
    }

    @Test
    void findAllOrderByUpdatedAtDesc() {
        problemService.create("A", null, Difficulty.EASY, Set.of());
        problemService.create("B", null, Difficulty.MEDIUM, Set.of());

        List<Problem> all = problemService.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void updateStatus() {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        problemService.updateStatus(p.getId(), SolveStatus.SOLVED);

        Problem updated = problemService.findById(p.getId());
        assertThat(updated.getStatus()).isEqualTo(SolveStatus.SOLVED);
    }

    @Test
    void deleteProblem() {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        problemService.delete(p.getId());

        List<Problem> all = problemService.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void countByStatus() {
        problemService.create("A", null, Difficulty.EASY, Set.of());
        Problem b = problemService.create("B", null, Difficulty.MEDIUM, Set.of());
        problemService.updateStatus(b.getId(), SolveStatus.SOLVED);

        assertThat(problemService.countByStatus(SolveStatus.NOT_ATTEMPTED)).isEqualTo(1);
        assertThat(problemService.countByStatus(SolveStatus.SOLVED)).isEqualTo(1);
    }

    @Test
    void findRecent() {
        for (int i = 0; i < 10; i++) {
            problemService.create("Problem " + i, null, Difficulty.EASY, Set.of());
        }

        List<Problem> recent = problemService.findRecent();
        assertThat(recent).hasSize(5);
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.problem.ProblemServiceTest"`
Expected: FAIL — ProblemService does not exist yet.

- [ ] **Step 3: Create ProblemService**

```java
package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;

    public List<Problem> findAll() {
        return problemRepository.findAllByOrderByUpdatedAtDesc();
    }

    public Problem findById(Long id) {
        return problemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));
    }

    public List<Problem> findRecent() {
        return problemRepository.findTop5ByOrderByUpdatedAtDesc();
    }

    public long count() {
        return problemRepository.count();
    }

    public long countByStatus(SolveStatus status) {
        return problemRepository.countByStatus(status);
    }

    public List<Map<String, Object>> getCategoryProgress() {
        return categoryRepository.findCategoryProgress().stream().map(row -> {
            String name = (String) row[0];
            long catTotal = (Long) row[1];
            long catSolved = (Long) row[2];
            return Map.<String, Object>of(
                "name", name,
                "total", catTotal,
                "solved", catSolved,
                "percent", catTotal > 0 ? (int)(catSolved * 100 / catTotal) : 0
            );
        }).toList();
    }

    @Transactional
    public Problem create(String title, String sourceUrl, Difficulty difficulty, Set<Long> categoryIds) {
        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setSourceUrl(sourceUrl);
        problem.setDifficulty(difficulty);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            problem.setCategories(categories);
        }
        return problemRepository.save(problem);
    }

    @Transactional
    public void update(Long id, String title, String sourceUrl, Difficulty difficulty, Set<Long> categoryIds) {
        Problem problem = findById(id);
        problem.setTitle(title);
        problem.setSourceUrl(sourceUrl);
        problem.setDifficulty(difficulty);
        problem.getCategories().clear();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            problem.setCategories(categories);
        }
    }

    @Transactional
    public void updateStatus(Long id, SolveStatus status) {
        Problem problem = findById(id);
        problem.setStatus(status);
    }

    @Transactional
    public void delete(Long id) {
        problemRepository.deleteById(id);
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.problem.ProblemServiceTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/problem/ProblemService.java src/test/java/lemuel/com/codingtest/problem/ProblemServiceTest.java
git commit -m "feat: add ProblemService with CRUD, status update, and tests"
```

---

### Task 8: Create SolutionService with tests

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/solution/SolutionService.java`
- Create: `src/test/java/lemuel/com/codingtest/solution/SolutionServiceTest.java`

- [ ] **Step 1: Write SolutionService tests**

```java
package lemuel.com.codingtest.solution;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SolutionServiceTest {

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private ProblemService problemService;

    @Test
    void createSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "System.out.println(1);", Language.JAVA, "brute force approach");

        assertThat(solution.getId()).isNotNull();
        assertThat(solution.getCode()).isEqualTo("System.out.println(1);");
        assertThat(solution.getLanguage()).isEqualTo(Language.JAVA);
        assertThat(solution.getNote()).isEqualTo("brute force approach");
    }

    @Test
    void updateSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "old code", Language.JAVA, "old note");

        solutionService.update(solution.getId(), "new code", Language.PYTHON, "new note");

        Solution updated = solutionService.findById(solution.getId());
        assertThat(updated.getCode()).isEqualTo("new code");
        assertThat(updated.getLanguage()).isEqualTo(Language.PYTHON);
        assertThat(updated.getNote()).isEqualTo("new note");
    }

    @Test
    void deleteSolution() {
        Problem problem = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution solution = solutionService.create(problem.getId(), "code", Language.JAVA, "");

        solutionService.delete(solution.getId());

        Problem reloaded = problemService.findById(problem.getId());
        assertThat(reloaded.getSolutions()).isEmpty();
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.solution.SolutionServiceTest"`
Expected: FAIL — SolutionService does not exist yet.

- [ ] **Step 3: Create SolutionService**

```java
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
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.solution.SolutionServiceTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/solution/SolutionService.java src/test/java/lemuel/com/codingtest/solution/SolutionServiceTest.java
git commit -m "feat: add SolutionService with CRUD operations and tests"
```

---

## Chunk 3: Thymeleaf Layout and Category UI

### Task 9: Create common Thymeleaf layout

**Files:**
- Create: `src/main/resources/templates/layout.html`

- [ ] **Step 1: Create layout.html with Bootstrap 5 + highlight.js CDN**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - $LAYOUT_TITLE">Coding Test Study</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
    <style>
        .badge-easy { background-color: #198754; }
        .badge-medium { background-color: #ffc107; color: #000; }
        .badge-hard { background-color: #dc3545; }
        .badge-solved { background-color: #198754; }
        .badge-failed { background-color: #dc3545; }
        .badge-retry { background-color: #ffc107; color: #000; }
        .badge-not-attempted { background-color: #6c757d; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" th:href="@{/}">Coding Test Study</a>
        <div class="navbar-nav">
            <a class="nav-link" th:href="@{/problems}">Problems</a>
            <a class="nav-link" th:href="@{/categories}">Categories</a>
        </div>
    </div>
</nav>
<div class="container">
    <div layout:fragment="content"></div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script>hljs.highlightAll();</script>
</body>
</html>
```

**Note:** All child templates use `layout:decorate="~{layout}"` and `layout:fragment="content"` to inject their content into the layout.

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/templates/layout.html
git commit -m "feat: add common Thymeleaf layout with Bootstrap 5 and highlight.js"
```

---

### Task 10: Create CategoryController and category list page

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/category/CategoryController.java`
- Create: `src/main/resources/templates/category/list.html`
- Create: `src/test/java/lemuel/com/codingtest/category/CategoryControllerTest.java`

- [ ] **Step 1: Write CategoryController tests**

```java
package lemuel.com.codingtest.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.category.CategoryControllerTest"`
Expected: FAIL — CategoryController does not exist yet.

- [ ] **Step 3: Create CategoryController**

```java
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
        model.addAttribute("categories", categoryService.findAll());
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
```

- [ ] **Step 4: Create category/list.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Categories</h2>
    </div>

    <form th:action="@{/categories}" method="post" class="row g-2 mb-4">
        <div class="col-md-3">
            <input type="text" class="form-control" name="name" placeholder="Category name" required>
        </div>
        <div class="col-md-5">
            <input type="text" class="form-control" name="description" placeholder="Description">
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">Add</button>
        </div>
    </form>

    <table class="table">
        <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Problems</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="entry : ${categoriesWithCount}">
                <td colspan="2">
                    <form th:action="@{/categories/{id}(id=${entry.category.id})}" method="post" class="d-flex gap-1">
                        <input type="text" class="form-control form-control-sm" name="name" th:value="${entry.category.name}" required style="max-width:200px">
                        <input type="text" class="form-control form-control-sm" name="description" th:value="${entry.category.description}">
                        <button type="submit" class="btn btn-sm btn-outline-primary">Save</button>
                    </form>
                </td>
                <td th:text="${entry.problemCount}"></td>
                <td>
                    <form th:action="@{/categories/{id}/delete(id=${entry.category.id})}" method="post" style="display:inline">
                        <button type="submit" class="btn btn-sm btn-outline-danger"
                                onclick="return confirm('Delete this category?')">Delete</button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>
</div>
</html>
```

**Note:** The controller passes `categoriesWithCount` — a list of objects with `category` and `problemCount` fields. Update `CategoryService` and `CategoryController` accordingly:

In `CategoryService`, add:
```java
public List<Map<String, Object>> findAllWithProblemCount() {
    return categoryRepository.findAllWithProblemCount().stream()
        .map(row -> Map.<String, Object>of("category", (Category) row[0], "problemCount", (Long) row[1]))
        .toList();
}
```

In `CategoryController.list()`, change to:
```java
model.addAttribute("categoriesWithCount", categoryService.findAllWithProblemCount());
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.category.CategoryControllerTest"`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/category/CategoryController.java src/main/resources/templates/category/list.html src/test/java/lemuel/com/codingtest/category/CategoryControllerTest.java
git commit -m "feat: add category management page with inline CRUD"
```

---

## Chunk 4: Problem CRUD UI

### Task 11: Create ProblemController and problem list page

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/problem/ProblemController.java`
- Create: `src/main/resources/templates/problem/list.html`
- Create: `src/test/java/lemuel/com/codingtest/problem/ProblemControllerTest.java`

- [ ] **Step 1: Write ProblemController tests**

```java
package lemuel.com.codingtest.problem;

import lemuel.com.codingtest.category.Category;
import lemuel.com.codingtest.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void listProblems() throws Exception {
        mockMvc.perform(get("/problems"))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/list"))
            .andExpect(model().attributeExists("problems"));
    }

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/problems/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/form"))
            .andExpect(model().attributeExists("categories"));
    }

    @Test
    void createProblem() throws Exception {
        Category dp = categoryService.create("DP", "");

        mockMvc.perform(post("/problems")
                .param("title", "피보나치 수")
                .param("sourceUrl", "https://boj.kr/1003")
                .param("difficulty", "EASY")
                .param("categoryIds", dp.getId().toString()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void showDetail() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/problems/" + p.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("problem/detail"))
            .andExpect(model().attributeExists("problem"));
    }

    @Test
    void updateStatus() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/status")
                .param("status", "SOLVED"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteProblem() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems"));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.problem.ProblemControllerTest"`
Expected: FAIL

- [ ] **Step 3: Create ProblemController**

```java
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
    public String list(Model model) {
        model.addAttribute("problems", problemService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("statuses", SolveStatus.values());
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
```

- [ ] **Step 4: Create problem/list.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Problems</h2>
        <a th:href="@{/problems/new}" class="btn btn-primary">New Problem</a>
    </div>

    <form th:action="@{/problems}" method="get" class="row g-2 mb-4">
        <div class="col-md-3">
            <input type="text" class="form-control" name="keyword" th:value="${keyword}" placeholder="Search by title...">
        </div>
        <div class="col-md-2">
            <select class="form-select" name="categoryId">
                <option value="">All Categories</option>
                <option th:each="cat : ${categories}" th:value="${cat.id}" th:text="${cat.name}"
                        th:selected="${categoryId != null && categoryId == cat.id}"></option>
            </select>
        </div>
        <div class="col-md-2">
            <select class="form-select" name="difficulty">
                <option value="">All Difficulties</option>
                <option th:each="d : ${difficulties}" th:value="${d}" th:text="${d}"
                        th:selected="${difficulty != null && difficulty.name() == d.name()}"></option>
            </select>
        </div>
        <div class="col-md-2">
            <select class="form-select" name="status">
                <option value="">All Statuses</option>
                <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}"
                        th:selected="${status != null && status.name() == s.name()}"></option>
            </select>
        </div>
        <div class="col-md-1">
            <button type="submit" class="btn btn-outline-primary w-100">Filter</button>
        </div>
    </form>

    <table class="table">
        <thead>
            <tr>
                <th><a th:href="@{/problems(keyword=${keyword},categoryId=${categoryId},difficulty=${difficulty},status=${status},sort='title')}">Title</a></th>
                <th><a th:href="@{/problems(keyword=${keyword},categoryId=${categoryId},difficulty=${difficulty},status=${status},sort='difficulty')}">Difficulty</a></th>
                <th>Categories</th>
                <th><a th:href="@{/problems(keyword=${keyword},categoryId=${categoryId},difficulty=${difficulty},status=${status},sort='status')}">Status</a></th>
                <th>Source</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="p : ${problems}">
                <td><a th:href="@{/problems/{id}(id=${p.id})}" th:text="${p.title}"></a></td>
                <td>
                    <span class="badge" th:classappend="${p.difficulty.name() == 'EASY' ? 'badge-easy' : (p.difficulty.name() == 'MEDIUM' ? 'badge-medium' : 'badge-hard')}"
                          th:text="${p.difficulty}"></span>
                </td>
                <td>
                    <span th:each="cat : ${p.categories}" class="badge bg-info me-1" th:text="${cat.name}"></span>
                </td>
                <td>
                    <span class="badge"
                          th:classappend="${p.status.name() == 'SOLVED' ? 'badge-solved' : (p.status.name() == 'FAILED' ? 'badge-failed' : (p.status.name() == 'RETRY' ? 'badge-retry' : 'badge-not-attempted'))}"
                          th:text="${p.status}"></span>
                </td>
                <td>
                    <a th:if="${p.sourceUrl}" th:href="${p.sourceUrl}" target="_blank" class="btn btn-sm btn-outline-secondary">Link</a>
                </td>
            </tr>
        </tbody>
    </table>
</div>
</html>
```

**Note:** The `ProblemController.list()` must handle filter parameters. Update `ProblemService` and `ProblemRepository` to support filtering:

In `ProblemRepository`, add:
```java
@Query("SELECT DISTINCT p FROM Problem p LEFT JOIN p.categories c WHERE " +
       "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
       "(:categoryId IS NULL OR c.id = :categoryId) AND " +
       "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
       "(:status IS NULL OR p.status = :status) " +
       "ORDER BY p.updatedAt DESC")
List<Problem> findByFilters(@Param("keyword") String keyword,
                             @Param("categoryId") Long categoryId,
                             @Param("difficulty") Difficulty difficulty,
                             @Param("status") SolveStatus status);
```

In `ProblemService`, add:
```java
public List<Problem> findByFilters(String keyword, Long categoryId, Difficulty difficulty, SolveStatus status) {
    return problemRepository.findByFilters(
        keyword != null && keyword.isBlank() ? null : keyword,
        categoryId, difficulty, status);
}
```

In `ProblemController.list()`, update to:
```java
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
```

In `ProblemService.findByFilters()`, add sorting support:
```java
public List<Problem> findByFilters(String keyword, Long categoryId, Difficulty difficulty, SolveStatus status, String sort) {
    List<Problem> results = problemRepository.findByFilters(
        keyword != null && keyword.isBlank() ? null : keyword,
        categoryId, difficulty, status);
    Comparator<Problem> comparator = switch (sort) {
        case "title" -> Comparator.comparing(Problem::getTitle);
        case "difficulty" -> Comparator.comparing(Problem::getDifficulty);
        case "status" -> Comparator.comparing(Problem::getStatus);
        default -> Comparator.comparing(Problem::getUpdatedAt, Comparator.reverseOrder());
    };
    results.sort(comparator);
    return results;
}
```

- [ ] **Step 5: Create problem/form.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <h2 th:text="${problem != null ? 'Edit Problem' : 'New Problem'}">New Problem</h2>

    <form th:action="${problem != null ? '/problems/' + problem.id : '/problems'}" method="post" class="mt-3">
        <div class="mb-3">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title" th:value="${problem?.title}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Source URL</label>
            <input type="url" class="form-control" name="sourceUrl" th:value="${problem?.sourceUrl}" placeholder="https://...">
        </div>
        <div class="mb-3">
            <label class="form-label">Difficulty</label>
            <select class="form-select" name="difficulty" required>
                <option th:each="d : ${difficulties}" th:value="${d}" th:text="${d}"
                        th:selected="${problem != null && problem.difficulty == d}"></option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Categories</label>
            <div th:each="cat : ${categories}" class="form-check">
                <input class="form-check-input" type="checkbox" name="categoryIds" th:value="${cat.id}"
                       th:checked="${problem != null && problem.categories.contains(cat)}">
                <label class="form-check-label" th:text="${cat.name}"></label>
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Save</button>
        <a th:href="${problem != null ? '/problems/' + problem.id : '/problems'}" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</html>
```

- [ ] **Step 6: Create problem/detail.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 th:text="${problem.title}"></h2>
        <div>
            <a th:href="@{/problems/{id}/edit(id=${problem.id})}" class="btn btn-outline-primary btn-sm">Edit</a>
            <form th:action="@{/problems/{id}/delete(id=${problem.id})}" method="post" style="display:inline">
                <button type="submit" class="btn btn-outline-danger btn-sm"
                        onclick="return confirm('Delete this problem and all solutions?')">Delete</button>
            </form>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-8">
            <p>
                <strong>Difficulty:</strong>
                <span class="badge" th:classappend="${problem.difficulty.name() == 'EASY' ? 'badge-easy' : (problem.difficulty.name() == 'MEDIUM' ? 'badge-medium' : 'badge-hard')}"
                      th:text="${problem.difficulty}"></span>
            </p>
            <p th:if="${!problem.categories.isEmpty()}">
                <strong>Categories:</strong>
                <span th:each="cat : ${problem.categories}" class="badge bg-info me-1" th:text="${cat.name}"></span>
            </p>
            <p th:if="${problem.sourceUrl}">
                <strong>Source:</strong> <a th:href="${problem.sourceUrl}" target="_blank" th:text="${problem.sourceUrl}"></a>
            </p>
        </div>
        <div class="col-md-4">
            <form th:action="@{/problems/{id}/status(id=${problem.id})}" method="post" class="d-flex gap-2">
                <select class="form-select form-select-sm" name="status">
                    <option th:each="s : ${statuses}" th:value="${s}" th:text="${s}" th:selected="${problem.status == s}"></option>
                </select>
                <button type="submit" class="btn btn-sm btn-outline-primary">Update</button>
            </form>
        </div>
    </div>

    <hr>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>Solutions</h4>
        <a th:href="@{/problems/{id}/solutions/new(id=${problem.id})}" class="btn btn-success btn-sm">Add Solution</a>
    </div>

    <div th:if="${problem.solutions.isEmpty()}" class="text-muted">No solutions yet.</div>

    <div th:each="sol : ${problem.solutions}" class="card mb-3">
        <div class="card-header d-flex justify-content-between">
            <span>
                <span class="badge bg-secondary" th:text="${sol.language}"></span>
                <small class="text-muted ms-2" th:text="${#temporals.format(sol.createdAt, 'yyyy-MM-dd HH:mm')}"></small>
            </span>
            <span>
                <a th:href="@{/problems/{pid}/solutions/{sid}/edit(pid=${problem.id}, sid=${sol.id})}" class="btn btn-outline-primary btn-sm">Edit</a>
                <form th:action="@{/problems/{pid}/solutions/{sid}/delete(pid=${problem.id}, sid=${sol.id})}" method="post" style="display:inline">
                    <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Delete this solution?')">Delete</button>
                </form>
            </span>
        </div>
        <div class="card-body">
            <pre><code th:class="${sol.language.name().toLowerCase()}" th:text="${sol.code}"></code></pre>
            <div th:if="${sol.note}" class="mt-2">
                <strong>Notes:</strong>
                <p th:text="${sol.note}"></p>
            </div>
        </div>
    </div>
</div>
</html>
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.problem.ProblemControllerTest"`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/problem/ProblemController.java src/main/resources/templates/problem/ src/test/java/lemuel/com/codingtest/problem/ProblemControllerTest.java
git commit -m "feat: add problem CRUD pages (list, detail, form) with controller and tests"
```

---

## Chunk 5: Solution CRUD UI

### Task 12: Create SolutionController and solution form page

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/solution/SolutionController.java`
- Create: `src/main/resources/templates/solution/form.html`
- Create: `src/test/java/lemuel/com/codingtest/solution/SolutionControllerTest.java`

- [ ] **Step 1: Write SolutionController tests**

```java
package lemuel.com.codingtest.solution;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.Problem;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SolutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SolutionService solutionService;

    @Test
    void showCreateForm() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/problems/" + p.getId() + "/solutions/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("solution/form"))
            .andExpect(model().attributeExists("problemId", "languages"));
    }

    @Test
    void createSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions")
                .param("code", "System.out.println(1);")
                .param("language", "JAVA")
                .param("note", "brute force"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }

    @Test
    void showEditForm() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "code", Language.JAVA, "note");

        mockMvc.perform(get("/problems/" + p.getId() + "/solutions/" + s.getId() + "/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("solution/form"))
            .andExpect(model().attributeExists("solution"));
    }

    @Test
    void updateSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "old", Language.JAVA, "");

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions/" + s.getId())
                .param("code", "new code")
                .param("language", "PYTHON")
                .param("note", "updated"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }

    @Test
    void deleteSolution() throws Exception {
        Problem p = problemService.create("Test", null, Difficulty.EASY, Set.of());
        Solution s = solutionService.create(p.getId(), "code", Language.JAVA, "");

        mockMvc.perform(post("/problems/" + p.getId() + "/solutions/" + s.getId() + "/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/problems/" + p.getId()));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.solution.SolutionControllerTest"`
Expected: FAIL

- [ ] **Step 3: Create SolutionController**

```java
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
```

- [ ] **Step 4: Create solution/form.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <h2 th:text="${solution != null ? 'Edit Solution' : 'New Solution'}">New Solution</h2>

    <form th:action="${solution != null ? '/problems/' + problemId + '/solutions/' + solution.id : '/problems/' + problemId + '/solutions'}" method="post" class="mt-3">
        <div class="mb-3">
            <label class="form-label">Language</label>
            <select class="form-select" name="language" required>
                <option th:each="lang : ${languages}" th:value="${lang}" th:text="${lang}"
                        th:selected="${solution != null && solution.language == lang}"></option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Code</label>
            <textarea class="form-control font-monospace" name="code" rows="15" required th:text="${solution?.code}"></textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Notes</label>
            <textarea class="form-control" name="note" rows="5" th:text="${solution?.note}" placeholder="Approach, key ideas, time complexity..."></textarea>
        </div>
        <button type="submit" class="btn btn-primary">Save</button>
        <a th:href="@{/problems/{id}(id=${problemId})}" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</html>
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.solution.SolutionControllerTest"`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/solution/SolutionController.java src/main/resources/templates/solution/form.html src/test/java/lemuel/com/codingtest/solution/SolutionControllerTest.java
git commit -m "feat: add solution CRUD with controller, form template, and tests"
```

---

## Chunk 6: Dashboard

### Task 13: Create DashboardController and dashboard page

**Files:**
- Create: `src/main/java/lemuel/com/codingtest/dashboard/DashboardController.java`
- Create: `src/main/resources/templates/dashboard.html`
- Create: `src/test/java/lemuel/com/codingtest/dashboard/DashboardControllerTest.java`

- [ ] **Step 1: Write DashboardController tests**

```java
package lemuel.com.codingtest.dashboard;

import lemuel.com.codingtest.problem.Difficulty;
import lemuel.com.codingtest.problem.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProblemService problemService;

    @Test
    void showDashboard() throws Exception {
        problemService.create("Test Problem", null, Difficulty.EASY, Set.of());

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("dashboard"))
            .andExpect(model().attributeExists("totalCount", "solvedCount", "failedCount", "retryCount", "notAttemptedCount", "recentProblems", "categoryProgress"));
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "lemuel.com.codingtest.dashboard.DashboardControllerTest"`
Expected: FAIL

- [ ] **Step 3: Create DashboardController**

```java
package lemuel.com.codingtest.dashboard;

import lemuel.com.codingtest.category.CategoryService;
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
    private final CategoryService categoryService;

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
```

- [ ] **Step 4: Create dashboard.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout}">
<div layout:fragment="content">
    <h2 class="mb-4">Dashboard</h2>

    <div class="row mb-4">
        <div class="col">
            <div class="card text-center">
                <div class="card-body">
                    <h5 class="card-title">Total</h5>
                    <h2 th:text="${totalCount}">0</h2>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card text-center border-success">
                <div class="card-body">
                    <h5 class="card-title text-success">Solved</h5>
                    <h2 th:text="${solvedCount}">0</h2>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card text-center border-danger">
                <div class="card-body">
                    <h5 class="card-title text-danger">Failed</h5>
                    <h2 th:text="${failedCount}">0</h2>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card text-center border-warning">
                <div class="card-body">
                    <h5 class="card-title text-warning">Retry</h5>
                    <h2 th:text="${retryCount}">0</h2>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card text-center border-secondary">
                <div class="card-body">
                    <h5 class="card-title text-secondary">Not Attempted</h5>
                    <h2 th:text="${notAttemptedCount}">0</h2>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-8">
            <h4>Recent Problems</h4>
            <table class="table" th:if="${!recentProblems.isEmpty()}">
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Difficulty</th>
                        <th>Status</th>
                        <th>Updated</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="p : ${recentProblems}">
                        <td><a th:href="@{/problems/{id}(id=${p.id})}" th:text="${p.title}"></a></td>
                        <td>
                            <span class="badge" th:classappend="${p.difficulty.name() == 'EASY' ? 'badge-easy' : (p.difficulty.name() == 'MEDIUM' ? 'badge-medium' : 'badge-hard')}"
                                  th:text="${p.difficulty}"></span>
                        </td>
                        <td>
                            <span class="badge"
                                  th:classappend="${p.status.name() == 'SOLVED' ? 'badge-solved' : (p.status.name() == 'FAILED' ? 'badge-failed' : (p.status.name() == 'RETRY' ? 'badge-retry' : 'badge-not-attempted'))}"
                                  th:text="${p.status}"></span>
                        </td>
                        <td th:text="${#temporals.format(p.updatedAt, 'yyyy-MM-dd HH:mm')}"></td>
                    </tr>
                </tbody>
            </table>
            <p th:if="${recentProblems.isEmpty()}" class="text-muted">No problems yet. <a th:href="@{/problems/new}">Create one!</a></p>
        </div>
        <div class="col-md-4">
            <h4>Category Progress</h4>
            <div th:if="${!categoryProgress.isEmpty()}">
                <div th:each="cp : ${categoryProgress}" class="mb-3">
                    <div class="d-flex justify-content-between">
                        <span th:text="${cp.name}"></span>
                        <small th:text="${cp.solved + '/' + cp.total}"></small>
                    </div>
                    <div class="progress">
                        <div class="progress-bar bg-success" role="progressbar"
                             th:style="'width: ' + ${cp.percent} + '%'"
                             th:attr="aria-valuenow=${cp.percent}" aria-valuemin="0" aria-valuemax="100">
                        </div>
                    </div>
                </div>
            </div>
            <p th:if="${categoryProgress.isEmpty()}" class="text-muted">No categories yet. <a th:href="@{/categories}">Add some!</a></p>
        </div>
    </div>
</div>
</html>
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew test --tests "lemuel.com.codingtest.dashboard.DashboardControllerTest"`
Expected: PASS

- [ ] **Step 6: Run all tests**

Run: `./gradlew test`
Expected: ALL PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/lemuel/com/codingtest/dashboard/ src/main/resources/templates/dashboard.html src/test/java/lemuel/com/codingtest/dashboard/
git commit -m "feat: add dashboard with status cards and recent problems"
```

---

## Chunk 7: Final Verification

### Task 14: Run full test suite and manual smoke test

- [ ] **Step 1: Run full test suite**

Run: `./gradlew test`
Expected: ALL PASS

- [ ] **Step 2: Start the application**

Run: `./gradlew bootRun`
Expected: Application starts on http://localhost:8080

- [ ] **Step 3: Manual smoke test checklist**

1. Visit http://localhost:8080 — dashboard loads
2. Visit /categories — add a category "DP"
3. Visit /problems/new — create a problem with category "DP"
4. Visit problem detail — verify info displays correctly
5. Add a solution with code — verify highlight.js renders
6. Change status to SOLVED — verify badge updates
7. Visit dashboard — verify counts update

- [ ] **Step 4: Final commit if any fixes needed**
