package jobrecommender.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Scanner;

@Configuration
public class CliConfig {
    @Bean
    public Scanner scanner() {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        return scanner;
    }
}
