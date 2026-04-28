package jobrecommender.service;

import jobrecommender.domain.Job;
import jobrecommender.dto.CreateJobDTO;
import jobrecommender.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class JobService {
    private final JobRepository repo;

    @Autowired
    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    public void addJob(String title, String company, Set<String> tags, int requiredExperience) {
        if (!repo.getJobs().containsKey(title)) {
            repo.putJob(title, new Job(title, company, tags, requiredExperience));
        }
    }

    public void addJob(CreateJobDTO dto) {
        addJob(dto.title(), dto.company(), dto.tags(), dto.requiredExperience());
    }

    public Map<String, Job> getJobs() {
        return repo.getJobs();
    }
}
