package jobrecommender.repository;

import jobrecommender.domain.User;

import java.util.Map;

public interface UserRepository {
    Map<String, User> getUsers();
    void putUser(User user);
}
