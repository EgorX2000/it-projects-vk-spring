package jobrecommender.repository;

import jobrecommender.domain.Job;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
@Profile("map")
public class JobRepositoryMap implements JobRepository {
    private final Map<String, Job> jobs = new ConcurrentSkipListMap<>();

    @Override
    public Map<String, Job> getJobs() {
        return Collections.unmodifiableMap(jobs);
    }

    @Override
    public void putJob(Job job) {
        jobs.putIfAbsent(job.getTitle(), job);
    }
}
