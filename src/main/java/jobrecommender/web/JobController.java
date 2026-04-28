package jobrecommender.web;

import jobrecommender.domain.Job;
import jobrecommender.dto.CreateJobDTO;
import jobrecommender.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/job")
    public void postJob(@RequestBody CreateJobDTO dto) {
        jobService.addJob(dto);
    }

    @GetMapping("/job-list")
    public Collection<Job> getJobList() {
        return jobService.getJobs().values();
    }
}
