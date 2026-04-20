package lemuel.com.codingtest.problem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findTop5ByOrderByUpdatedAtDesc();
    List<Problem> findAllByOrderByUpdatedAtDesc();
    long countByStatus(SolveStatus status);

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
}
