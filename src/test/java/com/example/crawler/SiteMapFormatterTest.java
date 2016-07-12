package com.example.crawler;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

import com.example.crawler.data.Page;
import com.example.crawler.data.PageLink;

public class SiteMapFormatterTest {

    private SiteMapFormatter underTest = new SiteMapFormatter();

    @Test
    public void testThatFormatProducesCorrectLinks() {
        Page root = createPage("http://test.com", 0);
        root.addExternalLinks(
                newArrayList(createLink("http://external.com/first"), createLink("http://external.com/second")));
        root.addStaticContentLinks(newArrayList(
                createLink("http://test.com/static/first.css"), createLink("http://test.com/static/second.css")));

        String result = underTest.format(root);

        assertThat(result, equalTo(new StringBuilder()
                .append("http://test.com\n")
                .append("\texternal: http://external.com/first\n")
                .append("\texternal: http://external.com/second\n")
                .append("\tstatic: http://test.com/static/first.css\n")
                .append("\tstatic: http://test.com/static/second.css\n")
                .toString()));
    }

    @Test
    public void testThatFormatProducesCorrectHierarchy() {
        Page root = createPage("http://test.com", 0);
        Page parentA = createPage("http://test.com/parentA", 1);
        root.addChild(parentA);
        Page parentB = createPage("http://test.com/parentB", 1);
        root.addChild(parentB);

        Page child1A = createPage("http://test.com/parentA/child1", 2);
        parentA.addChild(child1A);
        Page child2A = createPage("http://test.com/parentA/child2", 2);
        parentA.addChild(child2A);
        Page child1B = createPage("http://test.com/parentB/child1", 2);
        parentB.addChild(child1B);
        Page child2B = createPage("http://test.com/parentB/child2", 2);
        parentB.addChild(child2B);

        String result = underTest.format(root);

        assertThat(result, equalTo(new StringBuilder()
                .append("http://test.com\n")
                .append("\thttp://test.com/parentA\n")
                .append("\t\thttp://test.com/parentA/child1\n")
                .append("\t\thttp://test.com/parentA/child2\n")
                .append("\thttp://test.com/parentB\n")
                .append("\t\thttp://test.com/parentB/child1\n")
                .append("\t\thttp://test.com/parentB/child2\n")
                .toString()));
    }

    @Test
    public void testThatFormatProducesSinglePageWhenLinksAreNotProvided() {
        Page root = createPage("http://test.com", 0);
        assertThat(underTest.format(root), equalTo("http://test.com\n"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatFormatThrowsExceptionWhenPageIsNull() {
        underTest.format(null);
    }

    private Page createPage(String uri, int level) {
        return new Page(URI.create(uri), level);
    }

    private PageLink createLink(String uri) {
        return new PageLink(uri);
    }
}