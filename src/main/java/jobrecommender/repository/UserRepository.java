package jobrecommender.repository;

import jobrecommender.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class UserRepository {
    private final Map<String, User> users = new ConcurrentSkipListMap<>();

    public void putUser(String name, User user) {
            users.put(name, user);
    }

    public Map<String, User> getUsers() {
        return Map.copyOf(users);
    }
}
