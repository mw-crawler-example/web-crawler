package com.example.crawler.parse;

import java.util.Set;

import com.example.crawler.data.PageLink;
import com.google.common.collect.Sets;

public class PageParseResult {

    private final Set<PageLink> internalLinks = Sets.newHashSet();
    private final Set<PageLink> externalLinks = Sets.newHashSet();
    private final Set<PageLink> staticContentLinks = Sets.newHashSet();

    public void addInternalLink(final PageLink pageResource) {
        this.internalLinks.add(pageResource);
    }

    public void addExternalLink(final PageLink pageResource) {
        this.externalLinks.add(pageResource);
    }

    public void addStaticContentLink(final PageLink pageLink) {
        this.staticContentLinks.add(pageLink);
    }

    public Set<PageLink> getInternalLinks() {
        return internalLinks;
    }

    public Set<PageLink> getExternalLinks() {
        return externalLinks;
    }

    public Set<PageLink> getStaticContentLinks() {
        return staticContentLinks;
    }
}