package me.web_server.controller.rest.manage;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.SqlQueryException;
import me.web_server.service.IUserService;

@RequestMapping("/api/manage/clients")
@RestController
public class ClientsController {
    @Autowired
    private IUserService userService;

    @GetMapping
    public HashMap<String, Object> getClients(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("page") Integer page
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
}
