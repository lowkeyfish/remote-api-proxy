package com.yujunyang.remoteapiproxy.web.controller;

import com.yujunyang.remoteapiproxy.web.data.AppResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public AppResponse home() {
        return new AppResponse("hello");
    }
}
