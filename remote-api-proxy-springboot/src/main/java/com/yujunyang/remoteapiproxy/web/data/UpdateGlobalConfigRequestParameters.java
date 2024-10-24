package com.yujunyang.remoteapiproxy.web.data;

public class UpdateGlobalConfigRequestParameters {
    private boolean logOpened;
    private boolean proxyOpened;

    public boolean isLogOpened() {
        return logOpened;
    }

    public void setLogOpened(boolean logOpened) {
        this.logOpened = logOpened;
    }

    public boolean isProxyOpened() {
        return proxyOpened;
    }

    public void setProxyOpened(boolean proxyOpened) {
        this.proxyOpened = proxyOpened;
    }
}
