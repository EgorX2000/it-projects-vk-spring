package jobrecommender.repository;

import jobrecommender.domain.Job;

import java.util.Map;

public interface JobRepository {
    Map<String, Job> getJobs();
    void putJob(Job job);
}
