package com.example.crawler.download;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.crawler.data.Page;

@RunWith(MockitoJUnitRunner.class)
public class PageDownloaderTest {
    private static final URI PAGE_URI = URI.create("http://test.com/page");
    private static final int PAGE_LEVEL = 0;

    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private StatusLine statusLine;
    @Mock
    private HttpEntity httpEntity;
    @Captor
    private ArgumentCaptor<HttpGet> httpGetCaptor;
    private byte[] pageContent = new byte[] {1};

    @InjectMocks
    private PageDownloader underTest;

    @Before
    public void setUp() throws ClientProtocolException, IOException {
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(pageContent));
        when(httpEntity.getContentLength()).thenReturn((long) pageContent.length);
    }

    @Test
    public void testThatGetPageContentReturnsCorrectValue()
            throws PageDownloadException, ClientProtocolException, IOException {
        when(httpClient.execute(httpGetCaptor.capture())).thenReturn(httpResponse);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        byte[] result = underTest.getPageContent(new Page(PAGE_URI, PAGE_LEVEL));

        assertThat(httpGetCaptor.getValue().getURI(), equalTo(PAGE_URI));
        assertThat(result, equalTo(pageContent));
        verify(httpResponse).close();
    }

    @Test(expected = PageDownloadException.class)
    public void testThatGetPageContentThrowsExceptionWhenStatusIsNotFound()
            throws PageDownloadException, ClientProtocolException, IOException {
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);

        underTest.getPageContent(new Page(PAGE_URI, PAGE_LEVEL));
    }

    @Test(expected = PageDownloadException.class)
    public void testThatGetPageContentThrowsExceptionWhenExecuteResultsInIOException()
            throws PageDownloadException, ClientProtocolException, IOException {
        when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException());

        underTest.getPageContent(new Page(PAGE_URI, PAGE_LEVEL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatGetPageContentThrowsExceptionWhenPageIsNull() throws PageDownloadException {
        underTest.getPageContent(null);
    }
}