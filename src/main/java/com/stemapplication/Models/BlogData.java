package com.stemapplication.Models;

public class BlogData {
    private String title;
    private String description;
    private FeaturedPost featuredPost;

    // Constructors
    public BlogData() {
    }

    public BlogData(String title, String description, FeaturedPost featuredPost) {
        this.title = title;
        this.description = description;
        this.featuredPost = featuredPost;
    }

    // Getters and Setters
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

    public FeaturedPost getFeaturedPost() {
        return featuredPost;
    }

    public void setFeaturedPost(FeaturedPost featuredPost) {
        this.featuredPost = featuredPost;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogData blogData = (BlogData) o;
        return title.equals(blogData.title) &&
                description.equals(blogData.description);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    // toString
    @Override
    public String toString() {
        return "BlogData{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", featuredPost=" + featuredPost +
                '}';
    }
}