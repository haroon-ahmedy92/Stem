package com.stemapplication.Models;

import java.util.List;

public class BlogResponse {
    private BlogData blogData;
    private List<Category> categories;
    private List<BlogPost> blogPosts;
    private List<PopularArticle> popularArticles;
    private List<Comment> comments;

    // Constructors
    public BlogResponse() {
    }

    public BlogResponse(BlogData blogData, List<Category> categories,
                        List<BlogPost> blogPosts, List<PopularArticle> popularArticles,
                        List<Comment> comments) {
        this.blogData = blogData;
        this.categories = categories;
        this.blogPosts = blogPosts;
        this.popularArticles = popularArticles;
        this.comments = comments;
    }

    // Getters and Setters
    public BlogData getBlogData() {
        return blogData;
    }

    public void setBlogData(BlogData blogData) {
        this.blogData = blogData;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<BlogPost> getBlogPosts() {
        return blogPosts;
    }

    public void setBlogPosts(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    public List<PopularArticle> getPopularArticles() {
        return popularArticles;
    }

    public void setPopularArticles(List<PopularArticle> popularArticles) {
        this.popularArticles = popularArticles;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogResponse that = (BlogResponse) o;
        return blogData.equals(that.blogData) &&
                categories.equals(that.categories) &&
                blogPosts.equals(that.blogPosts);
    }

    @Override
    public int hashCode() {
        int result = blogData.hashCode();
        result = 31 * result + categories.hashCode();
        result = 31 * result + blogPosts.hashCode();
        return result;
    }

    // toString
    @Override
    public String toString() {
        return "BlogResponse{" +
                "blogData=" + blogData +
                ", categories=" + categories +
                ", blogPosts=" + blogPosts +
                ", popularArticles=" + popularArticles +
                ", comments=" + comments +
                '}';
    }
}