package jobrecommender.repository;

import jobrecommender.domain.User;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class UserRepository {
    /*private final Map<String, User> users = new ConcurrentSkipListMap<>();

    public void putUser(User user) {
        users.putIfAbsent(user.getName(), user);
    }

    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }*/

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, User> getUsers() {
        String sql = """
                select u.name, us.skill, u.experience
                from users u
                left join user_skills us
                on u.id = us.user_id
                """;

        return jdbcTemplate.query(sql, rs -> {
            record UserData(Set<String> skills, int experience) {}
            Map<String, UserData> usersDataDB = new HashMap<>();

            while (rs.next()) {
                String name = rs.getString("name");
                String skill = rs.getString("skill");
                int experience = rs.getInt("experience");

                usersDataDB.putIfAbsent(name, new UserData(new HashSet<>(), experience));
                if (skill != null) usersDataDB.get(name).skills().add(skill);
            }

            Map<String, User> users = new ConcurrentSkipListMap<>();
            for (String name : usersDataDB.keySet()) {
                users.put(name, new User(
                        name,
                        usersDataDB.get(name).skills(),
                        usersDataDB.get(name).experience()
                ));
            }

            return Collections.unmodifiableMap(users);
        });
    }

    @Transactional
    public void putUser(User user) {
        String sqlUser = """
                insert into users (name, experience) values
                (:name, :experience)
                on conflict (name) do nothing
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("experience", user.getExperience());
        int rowsAffected = jdbcTemplate.update(sqlUser, sqlParameterSource, keyHolder, new String[]{"id"});

        if (rowsAffected == 0 || keyHolder.getKey() == null) {
            return;
        }

        int userId = keyHolder.getKey().intValue();

        String sqlSkills = """
                insert into user_skills (user_id, skill) values
                (:userId, :skill)
                """;

        SqlParameterSource[] batchSqlParameterSource = user.getSkills().stream()
                .map(skill -> new MapSqlParameterSource()
                                .addValue("userId", userId)
                                .addValue("skill", skill)
                ).toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sqlSkills, batchSqlParameterSource);
    }
}
