package com.example.crawler.parse;

public class PageParseException extends Exception {
    private static final long serialVersionUID = -1447353291213686173L;

    public PageParseException(String message) {
        super(message);
    }

    public PageParseException(String message, Throwable cause) {
        super(message, cause);
    }
}