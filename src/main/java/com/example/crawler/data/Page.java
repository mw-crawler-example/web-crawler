package com.example.crawler.data;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;

public class Page implements Comparable<Page> {
    private final URI uri;
    private final int level;
    private final Set<Page> children;
    private final Set<PageLink> externalLinks;
    private final Set<PageLink> staticContentLinks;

    public Page(final URI uri, final int level) {
        this.uri = uri;
        this.level = level;
        this.children = Sets.newTreeSet();
        this.externalLinks = Sets.newTreeSet();
        this.staticContentLinks = Sets.newTreeSet();
    }

    public URI getURI() {
        return this.uri;
    }

    public int getLevel() {
        return this.level;
    }

    public void addChild(final Page page) {
        this.children.add(page);
    }

    public void addExternalLinks(final Collection<PageLink> externalLinks) {
        this.externalLinks.addAll(externalLinks);
    }

    public void addStaticContentLinks(final Collection<PageLink> staticContentLinks) {
        this.staticContentLinks.addAll(staticContentLinks);
    }

    public Set<Page> getChildren() {
        return this.children;
    }

    public Set<PageLink> getExternalLinks() {
        return this.externalLinks;
    }

    public Set<PageLink> getStaticContentLinks() {
        return this.staticContentLinks;
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

        Page page = (Page) obj;
        return Objects.equals(uri, page.uri);
    }

    @Override
    public int compareTo(final Page o) {
        return ComparisonChain.start().compare(uri, o.uri).result();
    }

    @Override
    public String toString() {
        return Objects.toString(uri);
    }
}