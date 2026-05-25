package jobrecommender.web;

import jobrecommender.domain.Job;
import jobrecommender.service.SuggestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SuggestController {
    private final SuggestService suggestService;

    public SuggestController(SuggestService suggestService) {
        this.suggestService = suggestService;
    }

    @GetMapping("/suggest/{username}")
    public List<Job> getSuggest(@PathVariable String username) {
        return suggestService.suggestJob(username);
    }
}
