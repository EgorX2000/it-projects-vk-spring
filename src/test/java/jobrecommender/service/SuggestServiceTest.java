package jobrecommender.service;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SuggestServiceTest {
    /*@Test
    void testSuggestJob_suggestTest() {
        UserService mockUserService = Mockito.mock(UserService.class);
        User alice = new User("alice", Set.of("java", "ml", "linux"), 2);
        when(mockUserService.getUsers()).thenReturn(Map.of(alice.getName(), alice));

        JobService mockJobService = Mockito.mock(JobService.class);
        Job frontend = new Job("Front_Dev", "Google", Set.of("js"), 3);
        Job python = new Job("Python_Engineer", "Yandex", Set.of("python", "ml"), 2);
        Job devops = new Job("Devops", "VK", Set.of("linux"), 3);
        when(mockJobService.getJobs()).thenReturn(Map.of(
                frontend.getTitle(), frontend,
                python.getTitle(), python,
                devops.getTitle(), devops
        ));

        SuggestService suggestService = new SuggestService(mockUserService, mockJobService);
        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(2, jobSuggestions.size());
        assertEquals(python, jobSuggestions.get(0));
        assertEquals(devops, jobSuggestions.get(1));
    }

    @Test
    void testSuggestJob_emptyJobsTest() {
        UserService mockUserService = Mockito.mock(UserService.class);
        User alice = new User("alice", Set.of("java", "ml", "linux"), 2);
        when(mockUserService.getUsers()).thenReturn(Map.of(alice.getName(), alice));

        JobService mockJobService = Mockito.mock(JobService.class);
        when(mockJobService.getJobs()).thenReturn(Collections.emptyMap());

        SuggestService suggestService = new SuggestService(mockUserService, mockJobService);
        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(0, jobSuggestions.size());
    }*/

    @Mock
    private UserService mockUserService;

    @Mock
    private JobService mockJobService;

    @InjectMocks
    private SuggestService suggestService;

    @Test
    public void testSuggestJob_suggestTest() {
        User alice = new User("alice", Set.of("java", "ml", "linux"), 2);
        when(mockUserService.getUsers()).thenReturn(Map.of(alice.getName(), alice));

        Job frontend = new Job("Front_Dev", "Google", Set.of("js"), 3);
        Job python = new Job("Python_Engineer", "Yandex", Set.of("python", "ml"), 2);
        Job devops = new Job("Devops", "VK", Set.of("linux"), 3);
        when(mockJobService.getJobs()).thenReturn(Map.of(
                frontend.getTitle(), frontend,
                python.getTitle(), python,
                devops.getTitle(), devops
        ));

        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(2, jobSuggestions.size());
        assertEquals(python, jobSuggestions.get(0));
        assertEquals(devops, jobSuggestions.get(1));
    }

    @Test
    public void testSuggestJob_emptyJobsTest() {
        User alice = new User("alice", Set.of("java", "ml", "linux"), 2);
        when(mockUserService.getUsers()).thenReturn(Map.of(alice.getName(), alice));

        when(mockJobService.getJobs()).thenReturn(Collections.emptyMap());

        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(0, jobSuggestions.size());
    }

    @Test
    public void testSuggestJob_singleVacancyTest() {
        User alice = new User("alice", Set.of("java", "ml", "linux"), 2);
        when(mockUserService.getUsers()).thenReturn(Map.of(alice.getName(), alice));

        Job devops = new Job("Devops", "VK", Set.of("linux"), 3);
        when(mockJobService.getJobs()).thenReturn(Map.of(devops.getTitle(), devops));

        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(1, jobSuggestions.size());
        assertEquals(devops, jobSuggestions.get(0));
    }

    @Test
    public void testSuggestJob_userNotFoundTest() {
        when(mockUserService.getUsers()).thenReturn(Collections.emptyMap());

        List<Job> jobSuggestions = suggestService.suggestJob("alice");

        assertEquals(0, jobSuggestions.size());
    }
}
