package com.example.crawler.parse;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

@RunWith(MockitoJUnitRunner.class)
public class PageParserTest {
    private static final Page PAGE = new Page(URI.create("http://test.com"), 0);
    private static final PageLink FIRST_LINK = new PageLink("http://test.com/first");
    private static final PageLink SECOND_LINK = new PageLink("http://test.com/second");
    private static final PageLink FIRST_EXTERNAL_LINK = new PageLink("http://first.com");
    private static final PageLink SECOND_EXTERNAL_LINK = new PageLink("http://second.com");

    @Mock
    private PageLinkFactory pageLinkFactory;

    @InjectMocks
    private PageParser underTest;

    @Before
    public void setUp() {
        when(pageLinkFactory.createPageLink(PAGE, "first")).thenReturn(FIRST_LINK);
        when(pageLinkFactory.createPageLink(PAGE, "second")).thenReturn(SECOND_LINK);
    }

    @Test
    public void testThatParseReturnsCorrectInternalLinks() throws PageParseException {
        PageParseResult result = underTest.parse(PAGE, "<a href='first'></a><a href='second'></a>".getBytes());

        assertThat(result.getInternalLinks(), containsInAnyOrder(FIRST_LINK, SECOND_LINK));
        assertThat(result.getExternalLinks(), empty());
        assertThat(result.getStaticContentLinks(), empty());
    }

    @Test
    public void testThatParseReturnsCorrectExternalLinks() throws PageParseException {
        when(pageLinkFactory.createPageLink(PAGE, "http://first.com")).thenReturn(FIRST_EXTERNAL_LINK);
        when(pageLinkFactory.createPageLink(PAGE, "http://second.com")).thenReturn(SECOND_EXTERNAL_LINK);

        PageParseResult result = underTest.parse(PAGE,
                "<a href='http://first.com'></a><a href='http://second.com'></a>".getBytes());

        assertThat(result.getInternalLinks(), empty());
        assertThat(result.getExternalLinks(), containsInAnyOrder(FIRST_EXTERNAL_LINK, SECOND_EXTERNAL_LINK));
        assertThat(result.getStaticContentLinks(), empty());
    }

    @Test
    public void testThatParseReturnsCorrectStaticContentLinks() throws PageParseException {
        PageParseResult result = underTest.parse(PAGE, "<img src='first' /><img src='second' />".getBytes());

        assertThat(result.getInternalLinks(), empty());
        assertThat(result.getExternalLinks(), empty());
        assertThat(result.getStaticContentLinks(), containsInAnyOrder(FIRST_LINK, SECOND_LINK));
    }

    @Test
    public void testThatParseReturnsEmptyResultWhenPageIsEmpty() throws PageParseException {
        PageParseResult result = underTest.parse(PAGE, "".getBytes());

        assertThat(result.getInternalLinks(), empty());
        assertThat(result.getExternalLinks(), empty());
        assertThat(result.getStaticContentLinks(), empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatParseThrowsExceptionWhenPageIsNull() throws PageParseException {
        underTest.parse(null, "<a href='first'></a>".getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatParseThrowsExceptionWhenPageContentIsNull() throws PageParseException {
        underTest.parse(PAGE, null);
    }
}