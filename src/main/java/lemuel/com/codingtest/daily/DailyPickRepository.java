package lemuel.com.codingtest.daily;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface DailyPickRepository extends JpaRepository<DailyPick, Long> {

    @EntityGraph(attributePaths = {"problem", "problem.categories"})
    Optional<DailyPick> findByPickDate(LocalDate pickDate);

    @Query("SELECT d.problem.id FROM DailyPick d")
    Set<Long> findPickedProblemIds();
}
