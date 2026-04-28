package jobrecommender.dto;

import java.util.Set;

public record CreateUserDTO(
        String name,
        Set<String> skills,
        int experience
) {}
