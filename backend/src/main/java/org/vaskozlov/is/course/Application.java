package org.vaskozlov.is.course;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@EnableJpaRepositories("org.vaskozlov.is.course.repository")
@EntityScan(basePackages = "org.vaskozlov.is.course.bean")
@SpringBootApplication(
        scanBasePackages = {"org.vaskozlov.is.course"}
)
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            String sql = """
                    CREATE OR REPLACE FUNCTION create_wallet() RETURNS TRIGGER AS
                    $$
                        BEGIN
                            INSERT INTO wallet (user_id, currency, balance)
                            VALUES (NEW.id, 'RUB', 0.0);
                            RETURN NEW;
                        END;
                    $$ LANGUAGE plpgsql;
                    
                    DROP TRIGGER IF EXISTS trig_create_wallet ON application_users;
                    
                    CREATE TRIGGER trig_create_wallet
                        AFTER INSERT
                        ON application_users
                        FOR EACH ROW
                    EXECUTE FUNCTION create_wallet();
                    """;
            jdbcTemplate.execute(sql);
        };
    }
}
