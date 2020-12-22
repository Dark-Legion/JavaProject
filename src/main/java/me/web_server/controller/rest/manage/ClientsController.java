package me.web_server.controller.rest.manage;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.SqlQueryException;
import me.web_server.service.UserService;

@RequestMapping("/api/manage/clients")
@RestController
public class ClientsController {
    @Autowired
    private UserService userService;

    @GetMapping
    public HashMap<String, Object> getClientListPageCount(
        @RequestParam("user") String username,
        @RequestParam("pass") String password
    ) throws SqlQueryException {
        return userService.handleRestRequest(() -> userService.getClientListPageCount(username, Hasher.hash(password)));
    }

    @GetMapping("/{page}")
    public HashMap<String, Object> getClientList(
        @PathVariable("page") Integer page,
        @RequestParam("user") String username,
        @RequestParam("pass") String password
    ) throws SqlQueryException {
        return userService.handleRestRequest(() -> userService.getClientList(username, Hasher.hash(password), page));
    }

    @PutMapping
    public HashMap<String, Object> addClient(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("client") String client,
        @RequestParam("is_company") Boolean isCompany
    ) throws SqlQueryException {
        return userService.handleRestRequest(
            () -> userService.addClient(
                username,
                Hasher.hash(password),
                client,
                isCompany
            )
        );
    }

    @PostMapping
    public HashMap<String, Object> changeClientName(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("client") String client,
        @RequestParam("new_name") String newName
    ) throws SqlQueryException {
        return null;
        // return userService.handleRestRequest(
        //     () -> userService.changeClientName(
        //         username,
        //         Hasher.hash(password),
        //         client,
        //         newName
        //     )
        // );
    }

    @DeleteMapping
    public HashMap<String, Object> deleteClient(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("client") String client
    ) throws SqlQueryException {
        return null;
        // return userService.handleRestRequest(
        //     () -> userService.changeClientName(
        //         username,
        //         Hasher.hash(password),
        //         client,
        //         newName
        //     )
        // );
    }
}
