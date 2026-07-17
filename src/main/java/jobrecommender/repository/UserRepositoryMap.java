package jobrecommender.repository;

import jobrecommender.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
@Profile("map")
public class UserRepositoryMap implements UserRepository {
    private final Map<String, User> users = new ConcurrentSkipListMap<>();

    @Override
    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    @Override
    public void putUser(User user) {
        users.putIfAbsent(user.getName(), user);
    }
}
