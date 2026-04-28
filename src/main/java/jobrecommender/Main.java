package jobrecommender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Scanner scanner() {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        return scanner;
    }
}
