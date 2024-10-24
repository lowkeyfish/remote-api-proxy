package com.yujunyang.remoteapiproxy.web.data;

public class AppResponse<T> {
    private int code;
    private String message;
    private T data;

    public AppResponse(
            int code,
            String message,
            T data
    ) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public AppResponse(
            int code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }

    public AppResponse(T data) {
        this.data = data;
        this.code = 0;
    }

    public AppResponse() {
        this.code = 0;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
