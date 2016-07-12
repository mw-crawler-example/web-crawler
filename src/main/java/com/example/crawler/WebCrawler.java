package com.example.crawler;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.appendIfMissing;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;
import com.example.crawler.download.PageDownloadException;
import com.example.crawler.download.PageDownloader;
import com.example.crawler.parse.PageParseException;
import com.example.crawler.parse.PageParseResult;
import com.example.crawler.parse.PageParser;

@Service
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    @Value("${com.example.crawler.maxDepth:5}")
    private int maxDepth;
    @Value("${com.example.crawler.maxPages:1000}")
    private int maxPages;
    @Autowired
    private PageDownloader pageDownloader;
    @Autowired
    private PageParser pageParser;

    public Page process(final String siteURL) {
        checkArgument(isNotBlank(siteURL), "Cannot create site map, site URL is empty");

        Page root = createRoot(siteURL);
        logger.info("Processing site: {}", root);

        Set<URI> discoveredURIs = Sets.newHashSet();
        Queue<Page> pagesToProcess = Lists.newLinkedList();

        pagesToProcess.offer(root);
        discoveredURIs.add(root.getURI());

        while (!pagesToProcess.isEmpty()) {
            Page currentPage = pagesToProcess.poll();
            PageParseResult result = processPage(currentPage);

            for (PageLink pageLink : result.getInternalLinks()) {
                // crawler should not process links already discovered
                // moreover crawler should not traverse hierarchy deeper than appropriate threshold
                if (!discoveredURIs.contains(pageLink.getURI())) {
                    if (currentPage.getLevel() >= maxDepth) {
                        logger.info("Skipping link {} for page: {}, maxDepth: {}, currentDepth: {}",
                                pageLink, currentPage, maxDepth, currentPage.getLevel());
                    } else if (discoveredURIs.size() >= maxPages) {
                        logger.info("Skipping link {} for page: {}, maxPages: {}, discoveredURIs: {}",
                                pageLink, currentPage, maxPages, discoveredURIs.size());
                    } else {
                        Page childPage = new Page(pageLink.getURI(), currentPage.getLevel() + 1);
                        currentPage.addChild(childPage);
                        pagesToProcess.add(childPage);
                        discoveredURIs.add(childPage.getURI());
                    }
                }
            }
            currentPage.addExternalLinks(result.getExternalLinks());
            currentPage.addStaticContentLinks(result.getStaticContentLinks());
        }

        return root;
    }

    private PageParseResult processPage(final Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing page: {}", page);
        }

        PageParseResult result = new PageParseResult();
        try {
            byte[] pageContent = pageDownloader.getPageContent(page);
            result = pageParser.parse(page, pageContent);
        } catch (PageDownloadException e) {
            logger.warn("Cannot download page: " + page.getURI(), e);
        } catch (PageParseException e) {
            logger.warn("Cannot parse page: " + page.getURI(), e);
        }
        return result;
    }

    private Page createRoot(String siteURL) {
        URI uri = URI.create(appendIfMissing(siteURL, "/")).normalize();
        return new Page(uri, 0);
    }
}