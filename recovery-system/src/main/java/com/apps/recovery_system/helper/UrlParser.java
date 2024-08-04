package com.apps.recovery_system.helper;

public class UrlParser {

    public static String parseUrlToJDBC(String url) {
        if (url.startsWith("postgres://")) {
            return "jdbc:postgresql://" + url.substring(11).replaceAll("/", "?");
        } else if (url.startsWith("mysql://")) {
            return "jdbc:mysql://" + url.substring(8).replaceAll("/", "?");
        }
        throw new IllegalArgumentException("Unsupported URL format: " + url);
    }
}