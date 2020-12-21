package me.web_server.controller.rest.manage;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.SqlQueryException;
import me.web_server.service.IUserService;

@RequestMapping("/api/manage/users")
@RestController
public class UsersController {
    @Autowired
    private IUserService userService;

    @PutMapping
    public HashMap<String, Object> addUser(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("new_user") String newUsername,
        @RequestParam("new_pass") String newPassword,
        @RequestParam("is_admin") Boolean isAdmin
    ) {
        String error = null;

        try {
            userService.addUser(
                username,
                Hasher.hash(password),
                newUsername,
                Hasher.hash(newPassword),
                isAdmin
            );
        } catch (SqlQueryException exception) {
            error = exception.getMessage();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("success", error == null);
        map.put("error", error);

        return map;
    }
}
