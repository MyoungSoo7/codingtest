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
