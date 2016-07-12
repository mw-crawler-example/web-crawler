package com.example.crawler.parse;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

public class PageLinkFactoryTest {
    private static final Page SITE_ROOT = new Page(URI.create("http://test.com"), 0);
    private static final Page PAGE = new Page(URI.create("http://test.com/somePage"), 1);

    private PageLinkFactory underTest = new PageLinkFactory();

    @Test
    public void testThatCreatePageLinkReturnsCorrectPageLinkWhenURIIsRelative() {
        PageLink result = underTest.createPageLink(SITE_ROOT, "somePage.html");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/somePage.html")));

        result = underTest.createPageLink(SITE_ROOT, "someParent/someDir/");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/someParent/someDir/")));

        result = underTest.createPageLink(SITE_ROOT, "somePage?param=value");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/somePage?param=value")));
    }

    @Test
    public void testThatCreatePageLinkReturnsCorrectPageLinkWhenURIIsTopLevelRelative() {
        PageLink result = underTest.createPageLink(PAGE, "/otherPage.html");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/otherPage.html")));

        result = underTest.createPageLink(PAGE, "/someParent/someDir/");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/someParent/someDir/")));

        result = underTest.createPageLink(PAGE, "/somePage?param=value");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/somePage?param=value")));
    }

    @Test
    public void testThatCreatePageLinkReturnsCorrectPageLinkWhenURIIsAbsolute() {
        PageLink result = underTest.createPageLink(SITE_ROOT, "http://test.com");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com")));

        result = underTest.createPageLink(SITE_ROOT, "http://test.com/someParent/someDir/");
        assertThat(result.getURI(), equalTo(URI.create("http://test.com/someParent/someDir/")));

        result = underTest.createPageLink(SITE_ROOT, "file://otherSite/parentDir/childDir/file");
        assertThat(result.getURI(), equalTo(URI.create("file://otherSite/parentDir/childDir/file")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatCreatePageLinkThrowsExceptionWhenParentPageIsNull() {
        underTest.createPageLink(null, "http://test.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatCreatePageLinkThrowsExceptionWhenLinkURIIsNull() {
        underTest.createPageLink(SITE_ROOT, null);
    }
}