package jobrecommender.service;

import jobrecommender.domain.User;
import jobrecommender.dto.CreateUserDTO;
import jobrecommender.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void addUser(String name, Set<String> skills, int experience) {
        repo.putUser(new User(name, skills, experience));
    }

    public void addUser(CreateUserDTO dto) {
        addUser(dto.name(), dto.skills(), dto.experience());
    }

    public Map<String, User> getUsers() {
        return repo.getUsers();
    }
}
