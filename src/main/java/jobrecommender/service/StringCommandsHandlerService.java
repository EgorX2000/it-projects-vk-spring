package jobrecommender.service;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class StringCommandsHandlerService {
    private final UserService userService;
    private final JobService jobService;
    private final SuggestService suggestService;
    private final StatService statService;
    private final LogsService commandLogger;

    public StringCommandsHandlerService(UserService userService, JobService jobService, StatService statService, SuggestService suggestService, LogsService commandLogger) {
        this.userService = userService;
        this.jobService = jobService;
        this.statService = statService;
        this.suggestService = suggestService;
        this.commandLogger = commandLogger;
    }

    public void handleUser(String[] subcommands) {
        String name = subcommands[1];
        Set<String> skills = new HashSet<>();
        int experience = 0;

        for (int i = 2; i < subcommands.length; i++) {
            if (subcommands[i].startsWith("--skills=")) {
                skills.addAll(List.of(subcommands[i].substring("--skills=".length()).split(",")));
            } else if (subcommands[i].startsWith("--exp=")) {
                experience = Integer.parseInt(subcommands[i].substring("--exp=".length()));
            }
        }

        userService.addUser(name, skills, experience);
    }

    public void handleJob(String[] subcommands) {
        String title = subcommands[1];
        String company = "";
        Set<String> tags = new HashSet<>();
        int requiredExperience = 0;

        for (int i = 2; i < subcommands.length; i++) {
            if (subcommands[i].startsWith("--company=")) {
                company = subcommands[i].substring("--company=".length());
            } else if (subcommands[i].startsWith("--tags=")) {
                tags.addAll(List.of(subcommands[i].substring("--tags=".length()).split(",")));
            } else if (subcommands[i].startsWith("--exp=")) {
                requiredExperience = Integer.parseInt(subcommands[i].substring("--exp=".length()));
            }
        }

        jobService.addJob(title, company, tags, requiredExperience);
    }

    public Collection<User> handleUserList() {
        return userService.getUsers().values();
    }

    public Collection<Job> handleJobList() {
        return jobService.getJobs().values();
    }

    public List<Job> handleSuggestions(String[] subcommands) {
        return suggestService.suggestJob(subcommands[1]);
    }

    public List<?> handleStatistics(String[] subcommands) {
        if (subcommands.length > 2) {
            return switch (subcommands[1]) {
                case "--exp" -> statService.jobsByExperience(Integer.parseInt(subcommands[2]));
                case "--match" -> statService.usersByMatches(Integer.parseInt(subcommands[2]));
                case "--top-skills" -> statService.topSkills(Integer.parseInt(subcommands[2]));
                default -> Collections.emptyList();
            };
        }

        return Collections.emptyList();
    }

    public List<String> handleHistory() {
        try {
            return commandLogger.getCommands();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
