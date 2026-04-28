package jobrecommender.web;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import jobrecommender.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatController {
    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/stat/exp/{n}")
    public List<Job> getStatExp(@PathVariable int n) {
        return statService.jobsByExperience(n);
    }

    @GetMapping("/stat/match/{n}")
    public List<User> getStatMatch(@PathVariable int n) {
        return statService.usersByMatches(n);
    }

    @GetMapping("/stat/top-skills/{n}")
    public List<String> getStatTopSkills(@PathVariable int n) {
        return statService.topSkills(n);
    }
}
