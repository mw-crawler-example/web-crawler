# Web Crawler

Simple web crawler that visits pages within provided domain.

## Supported functionality

* Crawler accepts starting URL of a domain it should process
* Processing is limited to single domain only. Crawler will not follow the links to external sites or additional subdomains.
* There is an option to configure additional restrictions related to page processing in application.properties
    * com.example.crawler.maxDepth - max crawl depth, crawler will not traverse further down in the website hierarchy
    * com.example.crawler.maxPages - max pages that crawler can collect
    * default configuration is:
```
    com.example.crawler.maxDepth=5
    com.example.crawler.maxPages=1000
```
* System outputs simple site map based on page hierarchy. Static and external links are prefixed with appropriate type, e.g.:
```
    http://site.com/
            http://site.com/pageA.html
                    static: http://site.com/css/general.css
            http://site.com/pageB.html
                    http://site.com/pageC.html
                    external: http://external.com
```

## TODO / Tradeoffs

* Crawler is not using robots.txt
* Crawler processes only pages with 200 response code
* Link normalisation is based on URI. Additional processing including encoding or query param normalisation is required.
* Multithreaded page fetching and parsing should improve crawler performance

## Environment

Program supports:
* Java 7
* Maven 3.3.9

Page content fetching is based on httpcomponents. Content analysis uses apache Tika

## Management

* Building application:
```
mvn clean verify
```
* Run example:
```
java -jar web-crawler-0.0.1-SNAPSHOT.jar http://website
```
