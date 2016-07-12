package com.example.crawler.parse;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;
import org.springframework.stereotype.Component;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

@Component
public class PageLinkFactory {

    public PageLink createPageLink(final Page parentPage, final String linkURI) {
        checkArgument(parentPage != null, "Cannot create page link, parent page is null");
        checkArgument(linkURI != null, "Cannot create page link, link URI is null");
        
        URI result = null;

        URI createdURI = URI.create(linkURI);
        if (createdURI.isAbsolute()) {
            result = createdURI;
        } else {
            result = URIUtils.resolve(parentPage.getURI(), createdURI);
        }

        return new PageLink(result.normalize());
    }
}