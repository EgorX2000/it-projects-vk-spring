package jobrecommender.dto;

import java.util.Set;

public record CreateJobDTO(
        String title,
        String company,
        Set<String> tags,
        int requiredExperience
) {}
