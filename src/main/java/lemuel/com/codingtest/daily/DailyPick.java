package lemuel.com.codingtest.daily;

import jakarta.persistence.*;
import lemuel.com.codingtest.problem.Problem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 날짜별 "오늘의 문제". 아침 발송(문제)과 저녁 발송(풀이)이 같은 문제를 보도록 날짜에 고정한다.
 * <p>
 * pickDate UNIQUE — 하루 한 문제. 한 번 뽑힌 문제는 다시 뽑지 않는다.
 */
@Entity
@Table(name = "daily_pick", uniqueConstraints = @UniqueConstraint(name = "uq_daily_pick_date", columnNames = "pick_date"))
@Getter
@Setter
@NoArgsConstructor
public class DailyPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pick_date", nullable = false)
    private LocalDate pickDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    /** 학습 경로 step 노트 — 아침 메시지의 힌트. 경로 밖 문제면 null. */
    @Column(length = 500)
    private String hint;

    /** 저녁에 저장된 풀이 id. null 이면 아직 풀이 없음. */
    private Long solutionId;
}
