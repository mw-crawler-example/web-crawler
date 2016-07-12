package com.example.crawler.parse;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

@Service
public class PageParser {
    private static final Logger logger = LoggerFactory.getLogger(PageParser.class);

    @Autowired
    private PageLinkFactory pageLinkFactory;

    public PageParseResult parse(final Page page, final byte[] pageContent) throws PageParseException {
        checkArgument(page != null, "Cannot parse page, page is null");
        checkArgument(pageContent != null, "Cannot parse page, pageContent is null");

        PageParseResult result = new PageParseResult();
        try {
            LinkContentHandler linkHandler = new LinkContentHandler();
            TeeContentHandler handler = new TeeContentHandler(linkHandler);
            new HtmlParser().parse(new ByteArrayInputStream(pageContent), handler, new Metadata(), new ParseContext());

            for (Link link : linkHandler.getLinks()) {
                processLink(result, page, link);
            }
        } catch (IOException | SAXException | TikaException e) {
            throw new PageParseException("Failed to parse page content, page: " + page, e);
        }
        return result;
    }

    private void processLink(final PageParseResult result, final Page parentPage, final Link link) {
        if (!isBlank(link.getUri())) {
            try {
                PageLink pageLink = pageLinkFactory.createPageLink(parentPage, link.getUri());
                if (link.isAnchor()) {
                    if (equalsIgnoreCase(parentPage.getURI().getHost(), pageLink.getURI().getHost())) {
                        result.addInternalLink(pageLink);
                    } else {
                        result.addExternalLink(pageLink);
                    }
                } else if (link.isImage() || link.isLink()) {
                    result.addStaticContentLink(pageLink);
                }
            } catch (Exception e) {
                logger.warn("Failed to process link: " + link, e);
            }
        }
    }
}