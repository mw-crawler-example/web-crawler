package com.example.crawler;

import static com.google.common.base.Preconditions.checkArgument;

import org.springframework.stereotype.Component;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

@Component
public class SiteMapFormatter {
    private static final String INDENTATION = "\t";
    private static final String NEW_LINE = "\n";

    public String format(final Page page) {
        checkArgument(page != null, "Cannot create site map, provided page is null");
        
        StringBuilder builder = new StringBuilder();
        append(builder, page);
        return builder.toString();
    }
    
    private void append(final StringBuilder builder, final Page page) {
        appendIndentation(builder, page.getLevel());
        builder.append(page.getURI()).append(NEW_LINE);

        for (Page childPage : page.getChildren()) {
            append(builder, childPage);
        }

        append(builder, "external: ", page.getExternalLinks(), page.getLevel() + 1);
        append(builder, "static: ", page.getStaticContentLinks(), page.getLevel() + 1);
    }

    private void append(final StringBuilder builder, final String prefix, final Iterable<PageLink> links, final int level) {
        for (PageLink link : links) {
            appendIndentation(builder, level);
            builder.append(prefix + link.getURI()).append(NEW_LINE);
        }
    }

    private void appendIndentation(final StringBuilder builder, final int level) {
        for (int i = 0; i < level; i++) {
            builder.append(INDENTATION);
        }
    }
}