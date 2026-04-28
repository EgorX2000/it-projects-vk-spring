package jobrecommender.service;

import jobrecommender.domain.User;
import jobrecommender.dto.CreateUserDTO;
import jobrecommender.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void addUser(String name, Set<String> skills, int experience) {
        if (!repo.getUsers().containsKey(name)) {
            repo.putUser(name, new User(name, skills, experience));
        }
    }

    public void addUser(CreateUserDTO dto) {
        addUser(dto.name(), dto.skills(), dto.experience());
    }

    public Map<String, User> getUsers() {
        return Map.copyOf(repo.getUsers());
    }
}
