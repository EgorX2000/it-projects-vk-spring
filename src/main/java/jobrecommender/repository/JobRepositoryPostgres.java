package jobrecommender.repository;

import jobrecommender.domain.Job;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
@ConditionalOnProperty(prefix = "app", name = "repository.type", havingValue = "postgres")
@AllArgsConstructor
public class JobRepositoryPostgres implements JobRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Job> getJobs() {
        String sql = """
                select j.title, j.company, jt.tag, j.required_experience
                from jobs j
                left join job_tags jt
                on j.id = jt.job_id
                """;

        return jdbcTemplate.query(sql, rs -> {
            record JobData(String company, Set<String> tags, int requiredExperience) {}
            Map<String, JobData> jobsDataDB = new HashMap<>();

            while (rs.next()) {
                String title = rs.getString("title");
                String company = rs.getString("company");
                String tag = rs.getString("tag");
                int requiredExperience = rs.getInt("required_experience");

                jobsDataDB.putIfAbsent(title, new JobData(company, new HashSet<>(), requiredExperience));
                if (tag != null) jobsDataDB.get(title).tags().add(tag);
            }

            Map<String, Job> jobs = new ConcurrentSkipListMap<>();
            for (String title : jobsDataDB.keySet()) {
                jobs.put(title, new Job(
                        title,
                        jobsDataDB.get(title).company(),
                        jobsDataDB.get(title).tags(),
                        jobsDataDB.get(title).requiredExperience()
                ));
            }

            return Collections.unmodifiableMap(jobs);
        });
    }

    @Override
    @Transactional
    public void putJob(Job job) {
        String sqlJob = """
                insert into jobs (title, company, required_experience) values
                (:title, :company, :requiredExperience)
                on conflict (title) do nothing
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("title", job.getTitle())
                .addValue("company", job.getCompany())
                .addValue("requiredExperience", job.getRequiredExperience());
        int rowsAffected = jdbcTemplate.update(sqlJob, sqlParameterSource, keyHolder, new String[]{"id"});

        if (rowsAffected == 0 || keyHolder.getKey() == null) {
            return;
        }

        int jobId = keyHolder.getKey().intValue();

        String sqlSkills = """
                insert into job_tags (job_id, tag) values
                (:jobId, :tag)
                """;

        SqlParameterSource[] batchSqlParameterSource = job.getTags().stream()
                .map(tag -> new MapSqlParameterSource()
                        .addValue("jobId", jobId)
                        .addValue("tag", tag)
                ).toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sqlSkills, batchSqlParameterSource);
    }
}
