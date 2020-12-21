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

@RequestMapping("/api/manage/products")
@RestController
public class ProductsController {
    @Autowired
    private IUserService userService;

    @PutMapping
    public HashMap<String, Object> addProduct(
        @RequestParam("user") String username,
        @RequestParam("pass") String password,
        @RequestParam("product") String product,
        @RequestParam("price") Double price
    ) {
        String error = null;

        try {
            userService.addProduct(
                username,
                Hasher.hash(password),
                product,
                price
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
