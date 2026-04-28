package jobrecommender.service;

import jobrecommender.domain.Job;
import jobrecommender.domain.JobMatch;
import jobrecommender.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SuggestService {
    private final UserService userService;
    private final JobService jobService;

    @Autowired
    public SuggestService(UserService userService, JobService jobService) {
        this.userService = userService;
        this.jobService = jobService;
    }

    public List<Job> suggestJob(String username) {
        List<JobMatch> matches = getJobMatches(username);

        return matches.stream().limit(2).map(JobMatch::getJob).toList();
    }

    public List<JobMatch> getJobMatches(String username) {
        User user = userService.getUsers().get(username);
        if (user == null) return Collections.emptyList();

        return jobService.getJobs().values().stream()
                .map(job -> new JobMatch(user, job))
                .filter(match -> match.getScore() > 0)
                .sorted((match1, match2) -> {
                    int scoreCompare = Double.compare(match2.getScore(), match1.getScore());

                    if (scoreCompare != 0) {
                        return scoreCompare;
                    }

                    return match1.getJob().getTitle().compareTo(match2.getJob().getTitle());
                })
                .toList();
    }
}
