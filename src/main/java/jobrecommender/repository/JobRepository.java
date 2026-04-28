package jobrecommender.repository;

import jobrecommender.domain.Job;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class JobRepository {
    private final Map<String, Job> jobs = new ConcurrentSkipListMap<>();

    public void putJob(String title, Job job) {
            jobs.put(title, job);
    }

    public Map<String, Job> getJobs() {
        return Map.copyOf(jobs);
    }
}
