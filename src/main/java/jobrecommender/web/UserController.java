package jobrecommender.web;

import jobrecommender.domain.User;
import jobrecommender.dto.CreateUserDTO;
import jobrecommender.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public void postUser(@RequestBody CreateUserDTO dto) {
        userService.addUser(dto);
    }

    @GetMapping("/user-list")
    public Collection<User> getUserList() {
        return userService.getUsers().values();
    }
}
