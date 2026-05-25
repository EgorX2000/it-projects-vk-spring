package jobrecommender.service;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatService {
    private final UserService userService;
    private final JobService jobService;

    public StatService(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    public List<Job> jobsByExperience(int n) {
        return jobService.getJobs().values().stream()
                .filter(job -> job.getRequiredExperience() >= n)
                .sorted(Comparator.comparing(Job::getTitle)).toList();
    }

    public List<User> usersByMatches(int n) {
        return userService.getUsers().values().stream()
                .filter(user -> {
                    long suitableJobsCount = jobService.getJobs().values().stream()
                            .filter(job -> user.countMatchingSkills(job) > 0)
                            .count();

                    return suitableJobsCount >= n;
                })
                .sorted(Comparator.comparing(User::getName))
                .toList();
    }

    public List<String> topSkills(int n) {
        Map<String, Long> skillsCounts = userService.getUsers().values().stream()
                .flatMap(user -> user.getSkills().stream())
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

        return skillsCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }
}
