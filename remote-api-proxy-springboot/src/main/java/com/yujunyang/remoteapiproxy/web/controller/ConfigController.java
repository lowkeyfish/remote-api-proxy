package com.yujunyang.remoteapiproxy.web.controller;

import com.yujunyang.remoteapiproxy.web.data.AppResponse;
import com.yujunyang.remoteapiproxy.web.data.UpdateGlobalConfigRequestParameters;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    @PutMapping("global")
    public AppResponse updateGlobalConfig(
            @RequestBody UpdateGlobalConfigRequestParameters requestParameters
    ) {


        return new AppResponse();
    }

    @GetMapping("global")
    public AppResponse getGlobalConfig() {

        return new AppResponse();
    }

    @PostMapping("")
    public AppResponse createConfig() {

        return new AppResponse();
    }

    @GetMapping("")
    public AppResponse query(
            @RequestParam("projectId") String projectId,
            @RequestParam("tag") String tag
    ) {

        return new AppResponse();
    }

    @DeleteMapping("")
    public AppResponse deleteAllConfig() {

        return new AppResponse();
    }

    @DeleteMapping("{configId}")
    public AppResponse deleteConfig(
            @RequestParam("projectId") String projectId,
            @PathVariable("configId") String configId
    ) {

        return new AppResponse();
    }

    @PutMapping("{configId}")
    public AppResponse updateConfig(
            @RequestParam("projectId") String projectId,
            @PathVariable("configId") String configId
    ) {

        return new AppResponse();
    }


}
