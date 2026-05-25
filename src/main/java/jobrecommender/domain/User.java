package jobrecommender.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class User {
    private final String name;
    private final Set<String> skills;
    private final int experience;

    public User(String name, Set<String> skills, int experience) {
        this.name = !name.isBlank() ? name : "Guest";
        this.skills = skills;
        this.experience = experience;
    }

    public int countMatchingSkills(Job job) {
        Set<String> match = new HashSet<>(skills);
        match.retainAll(job.getTags());

        return match.size();
    }

    public boolean hasEnoughExperience(Job job) {
        return experience >= job.getRequiredExperience();
    }

    public String getName() {
        return name;
    }

    public Set<String> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    public int getExperience() {
        return experience;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", name, skills.stream().sorted().collect(Collectors.joining(",")), experience);
    }
}
