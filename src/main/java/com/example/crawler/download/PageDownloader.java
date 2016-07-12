package com.example.crawler.download;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.compress.utils.IOUtils.closeQuietly;
import static org.apache.http.util.EntityUtils.toByteArray;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crawler.data.Page;

@Service
public class PageDownloader {

    @Autowired
    private CloseableHttpClient httpClient;

    public byte[] getPageContent(final Page page) throws PageDownloadException {
        checkArgument(page != null, "Cannot download content, provide page is null");

        CloseableHttpResponse httpResponse = null;
        byte[] pageContent = null;
        try {
            HttpGet httpGet = new HttpGet(page.getURI());
            httpResponse = httpClient.execute(httpGet);

            // Status code verification, expected code is 200
            // any other codes including 201 or 202 are rejected
            // downloader is not processing redirect requests
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                pageContent = toByteArray(httpEntity);
            } else {
                throw new PageDownloadException("Cannot download page: " + page + ", response code is: " + statusCode);
            }
        } catch (IOException e) {
            throw new PageDownloadException("Cannot download page: " + page, e);
        } finally {
            closeQuietly(httpResponse);
        }
        return pageContent;
    }
}