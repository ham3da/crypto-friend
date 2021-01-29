package com.ham3da.cryptofreind.news;

/**
 * Created by Ryan on 8/13/2017.
 */

public class NewsItem {

    public String articleTitle;
    public String articleURL;
    public String articleBody;
    public String imageURL;
    public String sourceName;
    public long publishedOn;

    public NewsItem(String articleTitle, String articleURL, String articleBody, String imageURL, String sourceName, long publishedOn) {
        this.articleTitle = articleTitle;
        this.articleURL = articleURL;
        this.articleBody = articleBody;
        this.imageURL = imageURL;
        this.sourceName = sourceName;
        this.publishedOn = publishedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsItem newsItem = (NewsItem) o;

        if (articleTitle == null || newsItem.articleTitle == null) {
            return false;
        }
        return articleTitle.equals(newsItem.articleTitle);
    }

    @Override
    public int hashCode() {
        int result = articleTitle != null ? articleTitle.hashCode() : 0;
        result = 31 * result + (articleURL != null ? articleURL.hashCode() : 0);
        result = 31 * result + (sourceName != null ? sourceName.hashCode() : 0);
        return result;
    }
}
