package com.github.derrop.cloudnettransformer.cloud.deserialized.proxy.tablist;

public class TabList {

    private final String header;
    private final String footer;

    public TabList(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFooter() {
        return this.footer;
    }
}
