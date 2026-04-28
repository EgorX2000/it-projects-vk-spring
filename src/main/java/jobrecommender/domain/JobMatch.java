package jobrecommender.domain;

public class JobMatch {
    private final Job job;
    private final double score;

    public JobMatch(User user, Job job) {
        this.job = job;
        this.score = calculateScore(user, job);
    }

    private double calculateScore(User user, Job job) {
        if (user.hasEnoughExperience(job)) {
            return user.countMatchingSkills(job);
        } else {
            return user.countMatchingSkills(job) / 2.0;
        }
    }

    public Job getJob() {
        return job;
    }

    public double getScore() {
        return score;
    }
}
