package com.yujunyang.remoteapiproxy.web.controller;

import com.yujunyang.remoteapiproxy.web.data.AppResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectConfig {

    @GetMapping("")
    public AppResponse allProject() {

        return new AppResponse();
    }

    @PostMapping("")
    public AppResponse createProject() {

        return new AppResponse();
    }

    @DeleteMapping("{projectId}")
    public AppResponse deleteProject(
            @PathVariable("projectId") String projectId
    ) {

        return new AppResponse();
    }

    @PutMapping("{projectId}")
    public AppResponse updateProject(
            @PathVariable("projectId") String projectId
    ) {

        return new AppResponse();
    }
}
