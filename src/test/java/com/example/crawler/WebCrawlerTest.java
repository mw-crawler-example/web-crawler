package com.example.crawler;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;
import com.example.crawler.download.PageDownloadException;
import com.example.crawler.download.PageDownloader;
import com.example.crawler.parse.PageParseException;
import com.example.crawler.parse.PageParseResult;
import com.example.crawler.parse.PageParser;

@RunWith(MockitoJUnitRunner.class)
public class WebCrawlerTest {
    private static final URI PARENT_URI = URI.create("http://test.com/");
    private static final URI CHILD_URI = URI.create("http://test.com/child");

    private static final PageLink PARENT_INTERNAL_LINK = new PageLink(PARENT_URI);
    private static final PageLink PARENT_EXTERNAL_LINK = new PageLink("http://parent.test.com");
    private static final PageLink PARENT_STATIC_CONTENT_LINK = new PageLink("http://test.com/static");

    private static final PageLink CHILD_INTERNAL_LINK = new PageLink(CHILD_URI);
    private static final PageLink CHILD_EXTERNAL_LINK = new PageLink("http://child.test.com");
    private static final PageLink CHILD_STATIC_CONTENT_LINK = new PageLink("http://test.com/child/static");

    @Mock
    private PageDownloader pageDownloader;
    @Mock
    private PageParser pageParser;
    @Captor
    private ArgumentCaptor<HttpGet> httpGetCaptor;

    @InjectMocks
    private WebCrawler underTest;

    private byte[] parentContent = "parent".getBytes();
    private byte[] childContent = "child".getBytes();

    @Before
    public void setUp() throws ClientProtocolException, IOException, PageDownloadException, PageParseException {
        setField(underTest, "maxDepth", 3);
        setField(underTest, "maxPages", 10);
        when(pageDownloader.getPageContent(
                argThat(Matchers.<Page> hasProperty("URI", equalTo(PARENT_URI))))).thenReturn(parentContent);
        when(pageDownloader.getPageContent(
                argThat(Matchers.<Page> hasProperty("URI", equalTo(CHILD_URI))))).thenReturn(childContent);

        PageParseResult parentResult = new PageParseResult();
        parentResult.addInternalLink(CHILD_INTERNAL_LINK);
        parentResult.addExternalLink(PARENT_EXTERNAL_LINK);
        parentResult.addStaticContentLink(PARENT_STATIC_CONTENT_LINK);

        when(pageParser.parse(
                argThat(Matchers.<Page> hasProperty("URI", equalTo(PARENT_URI))), eq(parentContent))).thenReturn(parentResult);

        PageParseResult childResult = new PageParseResult();
        childResult.addInternalLink(PARENT_INTERNAL_LINK);
        childResult.addExternalLink(CHILD_EXTERNAL_LINK);
        childResult.addStaticContentLink(CHILD_STATIC_CONTENT_LINK);

        when(pageParser.parse(
                argThat(Matchers.<Page> hasProperty("URI", equalTo(CHILD_URI))), eq(childContent))).thenReturn(childResult);
    }

    @Test
    public void testThatProcessCreatesPageHierarchy() {
        Page root = underTest.process(PARENT_URI.toString());

        assertThat(root, notNullValue());
        assertThat(root.getLevel(), equalTo(0));
        assertThat(root.getURI(), equalTo(PARENT_URI));
        assertThat(root.getExternalLinks(), contains(PARENT_EXTERNAL_LINK));
        assertThat(root.getStaticContentLinks(), contains(PARENT_STATIC_CONTENT_LINK));

        Set<Page> children = root.getChildren();
        assertThat(children, hasSize(1));

        Page child = children.iterator().next();

        assertThat(child, notNullValue());
        assertThat(child.getLevel(), equalTo(1));
        assertThat(child.getURI(), equalTo(CHILD_URI));
        assertThat(child.getExternalLinks(), contains(CHILD_EXTERNAL_LINK));
        assertThat(child.getStaticContentLinks(), contains(CHILD_STATIC_CONTENT_LINK));
        assertThat(child.getChildren(), empty());
    }

    @Test
    public void testThatProcessIsLimitedToMaxDepth() {
        setField(underTest, "maxDepth", 0);

        Page root = underTest.process(PARENT_URI.toString());

        assertThat(root, notNullValue());
        assertThat(root.getLevel(), equalTo(0));
        assertThat(root.getURI(), equalTo(PARENT_URI));
        assertThat(root.getExternalLinks(), contains(PARENT_EXTERNAL_LINK));
        assertThat(root.getStaticContentLinks(), contains(PARENT_STATIC_CONTENT_LINK));
        assertThat(root.getChildren(), empty());
    }

    @Test
    public void testThatProcessIsLimitedToMaxPages() {
        setField(underTest, "maxPages", 1);

        Page root = underTest.process(PARENT_URI.toString());

        assertThat(root, notNullValue());
        assertThat(root.getLevel(), equalTo(0));
        assertThat(root.getURI(), equalTo(PARENT_URI));
        assertThat(root.getExternalLinks(), contains(PARENT_EXTERNAL_LINK));
        assertThat(root.getStaticContentLinks(), contains(PARENT_STATIC_CONTENT_LINK));
        assertThat(root.getChildren(), empty());
    }
}