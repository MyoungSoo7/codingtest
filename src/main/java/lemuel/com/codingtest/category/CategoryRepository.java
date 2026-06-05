package lemuel.com.codingtest.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    @Query("SELECT c, COUNT(p) FROM Category c LEFT JOIN c.problems p GROUP BY c ORDER BY c.name")
    List<Object[]> findAllWithProblemCount();

    @Query("SELECT c.name, COUNT(p), SUM(CASE WHEN p.status = 'SOLVED' THEN 1 ELSE 0 END) FROM Category c LEFT JOIN c.problems p GROUP BY c.name ORDER BY c.name")
    List<Object[]> findCategoryProgress();
}
