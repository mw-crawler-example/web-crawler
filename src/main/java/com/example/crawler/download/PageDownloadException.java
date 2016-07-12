package com.example.crawler.download;

public class PageDownloadException extends Exception {
    private static final long serialVersionUID = 1226472432289129570L;

    public PageDownloadException(String message) {
        super(message);
    }

    public PageDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}