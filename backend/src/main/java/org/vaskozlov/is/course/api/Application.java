package org.vaskozlov.is.course.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("org.vaskozlov.is.course.repository")
@EntityScan(basePackages = "org.vaskozlov.is.course.bean")
@SpringBootApplication(
        scanBasePackages = {"org.vaskozlov.is.course"}
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
