package com.example.crawler.parse;

import static org.apache.poi.util.IOUtils.toByteArray;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.crawler.WebCrawlerRunner;
import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebCrawlerRunner.class)
public class PageParserITCase {
    private static final String baseURI = "http://test.com/currentDir/currentPage";
    private static final String PAGE_PATH = "/PageParserITCase-page.html";

    private static final PageLink[] INTERNAL_LINKS = new PageLink[] {
            new PageLink("http://test.com/menu/absolute"),
            new PageLink("http://test.com/menu/toplevel"),
            new PageLink("http://test.com/currentDir/relative")};

    private static final PageLink[] EXTERNAL_LINKS = new PageLink[] {
            new PageLink("http://other.test.com"),
            new PageLink("https://secure.site.com")};
    
    private static final PageLink[] STATIC_CONTENT_LINKS = new PageLink[] {
            new PageLink("http://test.com/cs/absolute.css"),
            new PageLink("http://test.com/cs/toplevel.css"),
            new PageLink("http://test.com/img/absolute.png"),
            new PageLink("http://test.com/currentDir/relative.png")};

    @Autowired
    private PageParser pageParser;

    private byte[] pageContent;

    @Before
    public void setUp() throws IOException {
        pageContent = toByteArray(getClass().getResourceAsStream(PAGE_PATH));
    }

    @Test
    public void testThatParseReturnsCorrectResult() throws PageParseException {
        PageParseResult result = pageParser.parse(new Page(URI.create(baseURI), 0), pageContent);
        
        assertThat(result.getInternalLinks(), containsInAnyOrder(INTERNAL_LINKS));
        assertThat(result.getExternalLinks(), containsInAnyOrder(EXTERNAL_LINKS));
        assertThat(result.getStaticContentLinks(), containsInAnyOrder(STATIC_CONTENT_LINKS));
    }
}