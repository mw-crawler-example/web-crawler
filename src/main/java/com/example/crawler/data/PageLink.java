package com.example.crawler.data;

import java.net.URI;
import java.util.Objects;

import com.google.common.collect.ComparisonChain;

public class PageLink implements Comparable<PageLink> {
    private final URI uri;

    public PageLink(final String uri) {
        this(URI.create(uri).normalize());
    }

    public PageLink(final URI uri) {
        this.uri = uri;
    }

    public URI getURI() {
        return this.uri;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        PageLink pageItem = (PageLink) obj;
        return Objects.equals(uri, pageItem.uri);
    }

    @Override
    public int compareTo(final PageLink o) {
        return ComparisonChain.start().compare(uri, o.uri).result();
    }

    @Override
    public String toString() {
        return Objects.toString(uri);
    }
}