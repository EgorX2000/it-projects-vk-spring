package jobrecommender.service.scheduled;

import jobrecommender.domain.JobMatch;
import jobrecommender.domain.User;
import jobrecommender.service.SuggestService;
import jobrecommender.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScheduledSuggester{
    private final UserService userService;
    private final SuggestService suggestService;

    public ScheduledSuggester(UserService userService, SuggestService suggestService) {
        this.userService = userService;
        this.suggestService = suggestService;
    }

    @Scheduled(fixedDelayString = "${app.scheduling.delay}")
    public void suggestForAllUsers() {
        if (userService.getUsers().isEmpty()) {
            return;
        }

        Map<User, JobMatch> bestMatchesForEachUser = userService.getUsers().values().stream()
                .map(user -> Map.entry(user, suggestService.getJobMatches(user.getName())))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));

        bestMatchesForEachUser.forEach((key, value) -> System.out.printf("%s, best offer - %s%n", key.getName(), value.getJob().toString()));
    }
}
