package com.stemapplication.Models;

import jakarta.persistence.Embeddable;

@Embeddable
public class PopularArticle {
    private String image;
    private String alt;
    private String title;
    private String views;

    // Constructors
    public PopularArticle() {
    }

    public PopularArticle(String image, String alt, String title, String views) {
        this.image = image;
        this.alt = alt;
        this.title = title;
        this.views = views;
    }

    // Getters and Setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopularArticle that = (PopularArticle) o;
        return image.equals(that.image) &&
                title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result = image.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

    // toString
    @Override
    public String toString() {
        return "PopularArticle{" +
                "image='" + image + '\'' +
                ", alt='" + alt + '\'' +
                ", title='" + title + '\'' +
                ", views='" + views + '\'' +
                '}';
    }
}