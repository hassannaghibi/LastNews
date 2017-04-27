package ir.ncompany.lastnews.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hassan on 6/10/16.
 */
public class RealmNews extends RealmObject {

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @PrimaryKey
    private String title;
    private String author;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    public RealmNews() {
    }

}