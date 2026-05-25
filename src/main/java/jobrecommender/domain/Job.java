package jobrecommender.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Job {
    private final String title;
    private final String company;
    private final Set<String> tags;
    private final int requiredExperience;

    public Job(String title, String company, Set<String> tags, int requiredExperience) {
        this.title = !title.isBlank() ? title : "Some job";
        this.company = !company.isBlank() ? company : "Somewhere";
        this.tags = tags;
        this.requiredExperience = requiredExperience;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public int getRequiredExperience() {
        return requiredExperience;
    }

    @Override
    public String toString() {
        return String.format("%s at %s", title, company);
    }
}
