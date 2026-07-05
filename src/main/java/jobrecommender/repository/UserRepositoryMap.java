package jobrecommender.repository;

import jobrecommender.domain.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
@ConditionalOnProperty(prefix = "app", name = "repository.type", havingValue = "map", matchIfMissing = true)
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
