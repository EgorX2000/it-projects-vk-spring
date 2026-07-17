package jobrecommender.service;

import jobrecommender.domain.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles({"test", "postgres"})
public class SuggestServiceIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @Autowired
    private UserService userService;
    @Autowired
    private JobService jobService;
    @Autowired
    private SuggestService suggestService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void testSuggestJob_suggestIntegrationTest() {
        userService.addUser("alice", Set.of("java", "ml", "linux"), 2);
        userService.addUser("bob", Set.of("c++"), 10);

        jobService.addJob("Front_Dev", "Google", Set.of("js"), 3);
        jobService.addJob("Python_Engineer", "Yandex", Set.of("python", "ml"), 2);
        jobService.addJob("Devops", "VK", Set.of("linux"), 3);

        List<Job> jobSuggestionsAlice = suggestService.suggestJob("alice");
        List<Job> jobSuggestionsBob = suggestService.suggestJob("bob");

        assertEquals(2, jobSuggestionsAlice.size());
        assertEquals("Python_Engineer", jobSuggestionsAlice.get(0).getTitle());
        assertEquals("Devops", jobSuggestionsAlice.get(1).getTitle());
        assertEquals(0, jobSuggestionsBob.size());
    }
}
