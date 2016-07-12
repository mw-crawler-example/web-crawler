package com.example.crawler;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.crawler.data.Page;

@SpringBootApplication
@EnableAutoConfiguration
public class WebCrawlerRunner {

    public static void main(String[] args) {
        checkArgument(args != null && args.length > 0 && isNotBlank(args[0]), "Site to crawl is null or empty");

        ApplicationContext context = SpringApplication.run(WebCrawlerRunner.class, args);

        WebCrawler crawler = context.getBean(WebCrawler.class);
        Page rootPage = crawler.process(args[0]);
        
        SiteMapFormatter formatter = context.getBean(SiteMapFormatter.class);
        System.out.println(formatter.format(rootPage));
    }
}