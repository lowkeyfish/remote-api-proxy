package com.yujunyang.remoteapiproxy.web.controller;

import java.util.ArrayList;

import com.yujunyang.remoteapiproxy.web.data.AppResponse;
import com.yujunyang.remoteapiproxy.web.data.CreateLogRequestParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    @GetMapping("")
    public AppResponse query(
            @RequestParam("projectId") String projectId,
            @RequestParam("requestBefore") long requestBeforeUnixMillisecondTimestamp
    ) {

        return new AppResponse(new ArrayList<>());
    }

    @PostMapping("")
    public AppResponse createLog(
            @RequestBody CreateLogRequestParameters requestParameters
    ) {

        return new AppResponse();
    }
}
