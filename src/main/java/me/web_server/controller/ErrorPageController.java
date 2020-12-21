package me.web_server.controller;

import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorPageController implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/error";
    }

    private ModelAndView handlePageError(HttpServletRequest request, Exception exception) {
        return ModelAndViews.INTERNAL_ERROR;
    }

    private ResponseEntity<HashMap<String, Object>> handleRestError(HttpServletRequest request, Exception exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new HashMap<>());
    }

    @RequestMapping("/error")
    @ResponseBody
    public Object handleError(HttpServletRequest request, Exception exception) {
        boolean restError = false;

        {
            String url = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString();
            restError |= url.equals("/api");
            restError |= url.startsWith("/api/");
        }

        return (
            restError ?
            handleRestError(request, exception) :
            handlePageError(request, exception)
        );
    }
}
